import time
import os
from playwright.sync_api import sync_playwright

def run_cuj():
    # Make sure verification directories exist
    os.makedirs("/home/jules/verification/screenshots", exist_ok=True)
    os.makedirs("/home/jules/verification/videos", exist_ok=True)
    os.makedirs(".eneik/records/design-check-4febcc3f-a581-4f19-91e6-da4a2c4bb13c", exist_ok=True)

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)

        # 1. Capture Desktop (1440px)
        context_desktop = browser.new_context(
            viewport={"width": 1440, "height": 900},
            record_video_dir="/home/jules/verification/videos"
        )
        page_desktop = context_desktop.new_page()
        page_desktop.goto("http://localhost:5173/")
        page_desktop.wait_for_timeout(2000) # Wait for svelte mounting & css

        # Take standard desktop screenshot for Eneik Design Gate
        page_desktop.screenshot(path=".eneik/records/design-check-4febcc3f-a581-4f19-91e6-da4a2c4bb13c/desktop-1440.png")
        # Take custom verification screenshot
        page_desktop.screenshot(path="/home/jules/verification/screenshots/verification-desktop.png")

        # Interact with the UI (select active role "преподаватель")
        page_desktop.get_by_role("button", name="преподаватель").click()
        page_desktop.wait_for_timeout(1000)

        # Search for "ФГОС" to demonstrate the acronym expansions search
        page_desktop.get_by_placeholder("Поиск материалов...").fill("ФГОС")
        page_desktop.wait_for_timeout(1000)

        context_desktop.close()

        # 2. Capture Mobile (375px)
        context_mobile = browser.new_context(
            viewport={"width": 375, "height": 812},
            user_agent="Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/04.1",
            record_video_dir="/home/jules/verification/videos"
        )
        page_mobile = context_mobile.new_page()
        page_mobile.goto("http://localhost:5173/")
        page_mobile.wait_for_timeout(2000) # Wait for svelte mounting & css

        # Take standard mobile screenshot for Eneik Design Gate
        page_mobile.screenshot(path=".eneik/records/design-check-4febcc3f-a581-4f19-91e6-da4a2c4bb13c/mobile-375.png")
        # Take custom verification screenshot
        page_mobile.screenshot(path="/home/jules/verification/screenshots/verification-mobile.png")

        context_mobile.close()
        browser.close()

if __name__ == "__main__":
    run_cuj()
    print("Screenshots captured successfully!")
