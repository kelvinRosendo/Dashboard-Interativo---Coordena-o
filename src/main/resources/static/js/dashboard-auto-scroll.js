(() => {
    const activeScrollers = new WeakMap();

    function setupAutoScroll(container) {
        const maxScroll = Math.max(0, container.scrollHeight - container.clientHeight);

        if (maxScroll <= 2) {
            teardownAutoScroll(container);
            return;
        }

        if (activeScrollers.has(container)) {
            activeScrollers.get(container).maxScroll = maxScroll;
            return;
        }

        const state = {
            maxScroll,
            speed: Number(container.dataset.scrollSpeed || 0.8),
            direction: 1,
            paused: false,
            rafId: null,
            waitUntil: performance.now() + 600,
            lastTime: performance.now(),
            position: container.scrollTop || 0,
            resumeTimeout: null
        };

        const step = (now) => {
            if (!state.paused && now >= state.waitUntil) {
                const delta = (now - state.lastTime) / 16.67;
                state.position += state.speed * state.direction * delta;

                if (state.position >= state.maxScroll) {
                    state.position = state.maxScroll;
                    state.direction = -1;
                    state.waitUntil = now + 900;
                } else if (state.position <= 0) {
                    state.position = 0;
                    state.direction = 1;
                    state.waitUntil = now + 900;
                }

                container.scrollTop = state.position;
            }

            state.lastTime = now;
            state.rafId = requestAnimationFrame(step);
        };

        const handleMouseEnter = () => {
            state.paused = true;
        };

        const handleMouseLeave = () => {
            state.paused = false;
            state.waitUntil = performance.now() + 500;
        };

        const handleWheel = () => {
            state.paused = true;
            clearTimeout(state.resumeTimeout);
            state.resumeTimeout = setTimeout(() => {
                state.paused = false;
                state.waitUntil = performance.now() + 700;
            }, 1400);
        };

        container.addEventListener("mouseenter", handleMouseEnter);
        container.addEventListener("mouseleave", handleMouseLeave);
        container.addEventListener("wheel", handleWheel, { passive: true });

        state.cleanup = () => {
            cancelAnimationFrame(state.rafId);
            clearTimeout(state.resumeTimeout);
            container.removeEventListener("mouseenter", handleMouseEnter);
            container.removeEventListener("mouseleave", handleMouseLeave);
            container.removeEventListener("wheel", handleWheel);
        };

        activeScrollers.set(container, state);
        state.rafId = requestAnimationFrame(step);
    }

    function teardownAutoScroll(container) {
        const state = activeScrollers.get(container);
        if (!state) {
            return;
        }

        state.cleanup();
        activeScrollers.delete(container);
    }

    function refreshAutoScroll() {
        document.querySelectorAll("[data-auto-scroll]").forEach(setupAutoScroll);
    }

    document.addEventListener("DOMContentLoaded", () => {
        refreshAutoScroll();
        setTimeout(refreshAutoScroll, 300);
        setTimeout(refreshAutoScroll, 1200);
    });

    window.addEventListener("load", refreshAutoScroll);
    window.addEventListener("resize", refreshAutoScroll);
})();
