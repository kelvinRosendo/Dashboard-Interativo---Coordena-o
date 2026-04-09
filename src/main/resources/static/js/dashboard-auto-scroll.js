document.addEventListener("DOMContentLoaded", () => {
    const containers = document.querySelectorAll("[data-auto-scroll]");

    containers.forEach((container) => {
        const maxScroll = container.scrollHeight - container.clientHeight;
        if (maxScroll <= 8) {
            return;
        }

        const speed = Number(container.dataset.scrollSpeed || 0.35);
        let direction = 1;
        let paused = false;
        let rafId = null;
        let lastTime = performance.now();
        let waitUntil = performance.now() + 1000;

        const step = (now) => {
            if (!paused) {
                if (now >= waitUntil) {
                    const delta = (now - lastTime) / 16.67;
                    container.scrollTop += speed * direction * delta;

                    if (container.scrollTop >= maxScroll) {
                        container.scrollTop = maxScroll;
                        direction = -1;
                        waitUntil = now + 1200;
                    } else if (container.scrollTop <= 0) {
                        container.scrollTop = 0;
                        direction = 1;
                        waitUntil = now + 1200;
                    }
                }
            }

            lastTime = now;
            rafId = requestAnimationFrame(step);
        };

        container.addEventListener("mouseenter", () => {
            paused = true;
        });

        container.addEventListener("mouseleave", () => {
            paused = false;
            waitUntil = performance.now() + 600;
        });

        container.addEventListener("wheel", () => {
            paused = true;
            clearTimeout(container._scrollResumeTimeout);
            container._scrollResumeTimeout = setTimeout(() => {
                paused = false;
                waitUntil = performance.now() + 900;
            }, 1500);
        }, { passive: true });

        rafId = requestAnimationFrame(step);

        window.addEventListener("beforeunload", () => {
            if (rafId) {
                cancelAnimationFrame(rafId);
            }
        });
    });
});
