import os
import subprocess
import time
import urllib.request
import pytest
import re
from PIL import Image, ImageChops
from playwright.sync_api import sync_playwright

# List of allowed Latin acronyms and tech terms that are not dictionary leaks in the UI
ALLOWED_LATIN_TERMS = {
    "pdf", "docx", "xlsx", "pptx", "lms", "api", "ui", "fbud", "git", "svg", "css", "html"
}

# Material icon names used in Svelte app that might be extracted if not fully skipped (just in case)
ALLOWED_ICON_NAMES = {
    "terminal", "menu_book", "settings_input_component", "dashboard", "info",
    "warning", "check_circle", "verified", "folder_open", "search", "close", "sensors", "memory"
}

def is_non_cyrillic_leak(word):
    # Strip non-alphabetic chars
    w_clean = re.sub(r'[^a-zA-Z]', '', word).lower()
    if len(w_clean) < 3:
        return False
    # Check if version info (like v1, v100)
    if re.match(r'^v\d+$', w_clean):
        return False
    if w_clean in ALLOWED_LATIN_TERMS or w_clean in ALLOWED_ICON_NAMES:
        return False
    return True

@pytest.fixture(scope="session", autouse=True)
def dev_server():
    # Verify if dev server is already running on port 5173
    try:
        with urllib.request.urlopen("http://localhost:5173/", timeout=1) as response:
            if response.status == 200:
                print("\n[Vite Server] Already running on port 5173.")
                yield
                return
    except Exception:
        pass

    # Start dev server
    print("\n[Vite Server] Starting server on port 5173...")
    proc = subprocess.Popen(
        ["npm", "run", "dev", "--", "--port", "5173", "--strictPort"],
        cwd="frontend",
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    # Wait for the port to be up and responsive
    connected = False
    for i in range(30):
        try:
            with urllib.request.urlopen("http://localhost:5173/", timeout=1) as response:
                if response.status == 200:
                    connected = True
                    break
        except Exception:
            time.sleep(0.5)

    if not connected:
        proc.terminate()
        raise RuntimeError("Vite dev server failed to start on port 5173.")

    print("[Vite Server] Started successfully.")
    yield

    print("[Vite Server] Stopping server...")
    proc.terminate()
    try:
        proc.wait(timeout=5)
    except subprocess.TimeoutExpired:
        proc.kill()
    print("[Vite Server] Stopped.")

def calculate_pixel_diff(img_path1, img_path2):
    """Calculates pixel difference percentage between two images with standard color tolerance."""
    if not os.path.exists(img_path1) or not os.path.exists(img_path2):
        return 100.0

    img1 = Image.open(img_path1).convert('RGB')
    img2 = Image.open(img_path2).convert('RGB')

    # Resize to match sizes if needed
    if img1.size != img2.size:
        img2 = img2.resize(img1.size, Image.Resampling.LANCZOS)

    width, height = img1.size
    total_pixels = width * height

    # Fast check using ImageChops
    diff = ImageChops.difference(img1, img2)
    bbox = diff.getbbox()
    if bbox is None:
        return 0.0

    # Strict point-by-point pixel difference with RGB threshold
    diff_pixels = 0
    img1_data = img1.load()
    img2_data = img2.load()

    for y in range(height):
        for x in range(width):
            r1, g1, b1 = img1_data[x, y]
            r2, g2, b2 = img2_data[x, y]
            # If colors differ by more than threshold of 15 in Euclidean distance
            if abs(r1 - r2) > 15 or abs(g1 - g2) > 15 or abs(b1 - b2) > 15:
                diff_pixels += 1

    percentage = (diff_pixels / total_pixels) * 100
    return percentage

def test_visual_regression():
    # Make temporary directory for test output
    os.makedirs("frontend/test_output", exist_ok=True)
    tmp_desktop_path = "frontend/test_output/tmp_desktop.png"
    tmp_mobile_path = "frontend/test_output/tmp_mobile.png"

    baseline_desktop = ".eneik/records/design-check-01bb8576-1b9d-4e05-bd1e-fd9166a0a311/desktop-1440.png"
    baseline_mobile = ".eneik/records/design-check-01bb8576-1b9d-4e05-bd1e-fd9166a0a311/mobile-375.png"

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)

        # 1. Capture Desktop View
        context_desktop = browser.new_context(viewport={"width": 1440, "height": 1050})
        page_desktop = context_desktop.new_page()
        page_desktop.goto("http://localhost:5173/")
        page_desktop.wait_for_timeout(2000)

        # Recreate input/error state exactly as the baseline screenshot
        page_desktop.get_by_label("ФИО заявителя (только кириллица)").fill("John Doe")
        page_desktop.get_by_label("Электронная почта").fill("john@example.com")
        page_desktop.get_by_label("Название документа").fill("Стандарт")
        page_desktop.get_by_label("Описание необходимых изменений").fill("Обновить информацию")
        page_desktop.get_by_role("button", name="Отправить запрос на актуализацию").click()
        page_desktop.wait_for_timeout(1000)

        page_desktop.screenshot(path=tmp_desktop_path, full_page=True)
        context_desktop.close()

        # 2. Capture Mobile View
        context_mobile = browser.new_context(
            viewport={"width": 375, "height": 1200},
            user_agent="Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/04.1"
        )
        page_mobile = context_mobile.new_page()
        page_mobile.goto("http://localhost:5173/")
        page_mobile.wait_for_timeout(2000)

        page_mobile.screenshot(path=tmp_mobile_path, full_page=True)
        context_mobile.close()

        browser.close()

    # Calculate differences
    diff_desktop_pct = calculate_pixel_diff(baseline_desktop, tmp_desktop_path)
    diff_mobile_pct = calculate_pixel_diff(baseline_mobile, tmp_mobile_path)

    print(f"\n[Visual Regression] Desktop diff percentage: {diff_desktop_pct:.4f}%")
    print(f"[Visual Regression] Mobile diff percentage: {diff_mobile_pct:.4f}%")

    # Assert that pixel differences are strictly below the 1% threshold
    assert diff_desktop_pct < 1.0, f"Desktop pixel diff {diff_desktop_pct:.4f}% is above 1.0% threshold"
    assert diff_mobile_pct < 1.0, f"Mobile pixel diff {diff_mobile_pct:.4f}% is above 1.0% threshold"

def test_localization_scanner():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        page.goto("http://localhost:5173/")
        page.wait_for_timeout(2000)

        # JavaScript E2E DOM crawler to extract visible user text nodes
        js_traverse = """
        () => {
            const textNodes = [];
            function walk(node) {
                if (node.nodeType === Node.ELEMENT_NODE) {
                    const tagName = node.tagName.toLowerCase();
                    if (['script', 'style', 'code', 'pre', 'svg'].includes(tagName)) {
                        return;
                    }
                    if (node.classList.contains('material-symbols-outlined')) {
                        return;
                    }
                    // Skip hidden/collapsed elements
                    const style = window.getComputedStyle(node);
                    if (style.display === 'none' || style.visibility === 'hidden') {
                        return;
                    }
                }
                if (node.nodeType === Node.TEXT_NODE) {
                    const text = node.nodeValue.trim();
                    if (text) {
                        textNodes.push(text);
                    }
                }
                for (let child = node.firstChild; child; child = child.nextSibling) {
                    walk(child);
                }
            }
            walk(document.body);
            return textNodes;
        }
        """

        texts = page.evaluate(js_traverse)
        browser.close()

    # Scan the extracted UI text nodes for non-Cyrillic dictionary words
    leaked_words = []
    for text in texts:
        # Split text into alphabetical words
        words = re.findall(r'[a-zA-Zа-яА-ЯёЁ\d\-]+', text)
        for w in words:
            # If word is entirely Latin alphabetic of length >= 3
            if re.match(r'^[a-zA-Z\-]+$', w):
                if is_non_cyrillic_leak(w):
                    leaked_words.append((w, text))

    print(f"\n[Localization Scan] Found {len(leaked_words)} leaked Latin/non-Cyrillic words.")
    if leaked_words:
        for word, context in leaked_words:
            print(f"  Leak: '{word}' in context '{context}'")

    assert len(leaked_words) == 0, f"Localization leak found! Non-Cyrillic dictionary words in UI text: {leaked_words}"
