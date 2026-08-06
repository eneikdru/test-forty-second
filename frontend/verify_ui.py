import time
import os
from playwright.sync_api import sync_playwright

def run_cuj():
    # Make sure verification directories exist
    os.makedirs("/home/jules/verification/screenshots", exist_ok=True)
    os.makedirs("/home/jules/verification/videos", exist_ok=True)
    target_dir = ".eneik/records/design-check-e970d9c2-95a5-443c-897f-b182de15dbfd"
    os.makedirs(target_dir, exist_ok=True)

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)

        # 1. Capture Desktop (1440px)
        context_desktop = browser.new_context(
            viewport={"width": 1440, "height": 1050},
            record_video_dir="/home/jules/verification/videos"
        )
        page_desktop = context_desktop.new_page()
        # Navigate to the integrations settings page
        page_desktop.goto("http://localhost:5173/?view=settings")
        page_desktop.wait_for_timeout(2000) # Wait for Svelte mounting and styles

        # Trigger LMS secure credentials config modal to show securely saves UI
        page_desktop.get_by_role("button", name="Manage Integration").first.click()
        page_desktop.wait_for_timeout(1000)

        # Enter credentials
        page_desktop.get_by_placeholder("Enter secure bearer authentication token...").fill("canvas-token-secure-1234")
        page_desktop.wait_for_timeout(500)

        # Take standard desktop screenshot for Eneik Design Gate
        page_desktop.screenshot(path=f"{target_dir}/desktop-1440.png", full_page=True)
        # Take custom verification screenshot
        page_desktop.screenshot(path="/home/jules/verification/screenshots/verification-desktop.png", full_page=True)

        context_desktop.close()

        # 2. Capture Mobile (375px)
        context_mobile = browser.new_context(
            viewport={"width": 375, "height": 1200},
            user_agent="Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/04.1",
            record_video_dir="/home/jules/verification/videos"
        )
        page_mobile = context_mobile.new_page()
        # Navigate to settings page
        page_mobile.goto("http://localhost:5173/?view=settings")
        page_mobile.wait_for_timeout(2000) # Wait for Svelte mounting and styles

        # Take standard mobile screenshot for Eneik Design Gate
        page_mobile.screenshot(path=f"{target_dir}/mobile-375.png", full_page=True)
        # Take custom verification screenshot
        page_mobile.screenshot(path="/home/jules/verification/screenshots/verification-mobile.png", full_page=True)

        context_mobile.close()
        browser.close()

if __name__ == "__main__":
    run_cuj()
    print("Screenshots captured successfully!")
