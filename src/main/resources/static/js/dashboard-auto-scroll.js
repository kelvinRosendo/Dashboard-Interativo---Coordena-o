(() => {
    const activeScrollers = new WeakMap();

    function setupAutoScroll(container) {
        if (window.innerWidth <= 1200) {
            teardownAutoScroll(container);
            return;
        }

        const maxScroll = Math.max(0, container.scrollHeight - container.clientHeight);

        if (maxScroll <= 24) {
            teardownAutoScroll(container);
            return;
        }

        if (activeScrollers.has(container)) {
            activeScrollers.get(container).maxScroll = maxScroll;
            return;
        }

        const state = {
            maxScroll,
            speed: Number(container.dataset.scrollSpeed || 0.35),
            direction: 1,
            paused: false,
            rafId: null,
            waitUntil: performance.now() + 1800,
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
                    state.waitUntil = now + 2200;
                } else if (state.position <= 0) {
                    state.position = 0;
                    state.direction = 1;
                    state.waitUntil = now + 2200;
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
            state.waitUntil = performance.now() + 1200;
        };

        const handleWheel = () => {
            state.paused = true;
            clearTimeout(state.resumeTimeout);
            state.resumeTimeout = setTimeout(() => {
                state.paused = false;
                state.waitUntil = performance.now() + 1200;
            }, 1800);
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
