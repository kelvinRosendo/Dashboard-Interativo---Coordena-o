const SIDEBAR_STORAGE_KEY = "dashboardSidebarCollapsed";

function isDashboardTvMode() {
    return document.body.classList.contains("tv-dashboard-mode")
        || document.querySelector(".app-shell--dashboard-tv") !== null;
}

function resolveInitialSidebarCollapsed(shell) {
    if (isDashboardTvMode()) {
        return true;
    }

    try {
        const savedState = window.localStorage.getItem(SIDEBAR_STORAGE_KEY);
        const isTvShell = Boolean(shell && shell.classList.contains("app-shell--tv"));
        const isCompactViewport = window.matchMedia("(max-width: 980px)").matches;

        if (savedState === null) {
            return isTvShell || isCompactViewport;
        }

        return savedState === "true";
    } catch {
        return false;
    }
}

function updateSidebarToggle(toggle, toggleIcon, collapsed) {
    if (toggle) {
        toggle.setAttribute("aria-expanded", String(!collapsed));
        toggle.setAttribute("aria-label", collapsed ? "Expandir menu" : "Recolher menu");
        toggle.setAttribute("title", collapsed ? "Expandir menu" : "Recolher menu");
    }

    if (toggleIcon) {
        toggleIcon.textContent = collapsed ? ">" : "<";
    }
}

function applySidebarBootstrap() {
    const sidebar = document.querySelector(".app-sidebar");
    const shell = document.querySelector(".app-shell");

    if (!sidebar || isDashboardTvMode()) {
        document.documentElement.classList.remove("sidebar-collapsed-pending");
        return;
    }

    const collapsed = resolveInitialSidebarCollapsed(shell);

    document.documentElement.classList.toggle("sidebar-collapsed-pending", collapsed);
    sidebar.classList.toggle("app-sidebar--collapsed", collapsed);

    if (shell) {
        shell.classList.toggle("app-shell--sidebar-collapsed", collapsed);
        shell.querySelectorAll(".app-main").forEach((main) => {
            main.classList.toggle("app-main--sidebar-collapsed", collapsed);
        });
    }

    updateSidebarToggle(
        sidebar.querySelector("[data-sidebar-toggle]"),
        sidebar.querySelector("[data-sidebar-toggle-icon]"),
        collapsed
    );
}

applySidebarBootstrap();

document.addEventListener("DOMContentLoaded", () => {
    initSidebar();
    initConflictModal();
    initDashboardTimer();
    initWeekFocusPreview();
    initTvInfiniteAutoScroll();
});

function initSidebar() {
    const sidebar = document.querySelector(".app-sidebar");

    if (!sidebar || isDashboardTvMode()) {
        document.documentElement.classList.remove("sidebar-collapsed-pending");
        return;
    }

    const shell = sidebar.closest(".app-shell");
    const toggle = sidebar.querySelector("[data-sidebar-toggle]");
    const toggleIcon = sidebar.querySelector("[data-sidebar-toggle-icon]");
    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    const setCollapsed = (collapsed, persist = true) => {
        sidebar.classList.toggle("app-sidebar--collapsed", collapsed);
        document.documentElement.classList.toggle("sidebar-collapsed-pending", collapsed);

        if (shell) {
            shell.classList.toggle("app-shell--sidebar-collapsed", collapsed);
            shell.querySelectorAll(".app-main").forEach((main) => {
                main.classList.toggle("app-main--sidebar-collapsed", collapsed);
            });
        }

        updateSidebarToggle(toggle, toggleIcon, collapsed);

        if (persist) {
            try {
                window.localStorage.setItem(SIDEBAR_STORAGE_KEY, String(collapsed));
            } catch {}
        }
    };

    setCollapsed(resolveInitialSidebarCollapsed(shell), false);
    document.documentElement.classList.remove("sidebar-collapsed-pending");

    if (toggle) {
        toggle.addEventListener("click", () => {
            const collapsed = !sidebar.classList.contains("app-sidebar--collapsed");
            setCollapsed(collapsed);
        });
    }

    if (!prefersReducedMotion) {
        window.requestAnimationFrame(() => {
            sidebar.classList.add("app-sidebar--ready");
            if (shell) {
                shell.classList.add("app-shell--sidebar-ready");
            }
        });
    }
}
function initConflictModal() {
    const modal = document.querySelector("[data-conflito-modal]");

    if (!modal) {
        return;
    }

    const closeModal = () => {
        modal.classList.remove("conflito-modal--open");
    };

    modal.classList.add("conflito-modal--open");

    modal.querySelectorAll("[data-conflito-close]").forEach((element) => {
        element.addEventListener("click", closeModal);
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeModal();
        }
    });
}

function initDashboardTimer() {
    const timer = document.querySelector("[data-dashboard-timer]");

    if (!timer) {
        return;
    }

    const countdown = timer.querySelector("[data-dashboard-countdown]");
    const target = timer.dataset.dashboardTarget || resolverDashboardTarget();
    const duration = Number.parseInt(timer.dataset.dashboardDuration || "30", 10);
    let remaining = Number.isFinite(duration) && duration > 0 ? duration : 30;

    const render = () => {
        if (countdown) {
            countdown.textContent = `${remaining}s`;
        }
    };

    render();

    const interval = window.setInterval(() => {
        remaining -= 1;
        render();

        if (remaining <= 0) {
            window.clearInterval(interval);
            redirectWithTvTransition(target);
        }
    }, 1000);
}

function resolverDashboardTarget() {
    return window.location.pathname.includes("/tv/calendario")
        ? "/tv/semana?modo=dashboard"
        : "/tv/calendario?modo=dashboard";
}

function redirectWithTvTransition(target) {
    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    if (prefersReducedMotion) {
        window.location.assign(target);
        return;
    }

    document.body.classList.add("tv-page-transitioning");
    window.setTimeout(() => {
        window.location.assign(target);
    }, 520);
}

function initWeekFocusPreview() {
    const form = document.querySelector(".week-focus-form");

    if (!form) {
        return;
    }

    const previewSegment = document.getElementById("previewSegment");
    const previewTitle = document.getElementById("previewTitle");
    const previewDescription = document.getElementById("previewDescription");
    const previewDate = document.getElementById("previewDate");
    const previewStatus = document.getElementById("previewStatus");
    const ativaInput = document.getElementById("ativaInput");

    const segmentoSelect = document.getElementById("segmento");
    const tituloInput = document.getElementById("titulo");
    const descricaoTextarea = document.getElementById("descricao");
    const dataInicioInput = document.getElementById("dataInicio");
    const dataFimInput = document.getElementById("dataFim");

    function updatePreview() {
        // Segment
        const segmentText = segmentoSelect?.selectedOptions?.[0]?.text || "Segmento";
        if (previewSegment) {
            previewSegment.textContent = segmentText;
        }

        // Title
        const titleText = tituloInput?.value?.trim() || "Digite o título...";
        if (previewTitle) {
            previewTitle.textContent = titleText;
        }

        // Description
        const descText = descricaoTextarea?.value?.trim() || "Sua descrição aparecerá aqui conforme você digita.";
        if (previewDescription) {
            previewDescription.textContent = descText;
        }

        // Date range
        const startDate = dataInicioInput?.value;
        const endDate = dataFimInput?.value;
        const dateText = (startDate && endDate)
            ? `Período: ${formatDateForDisplay(startDate)} — ${formatDateForDisplay(endDate)}`
            : "Período: --";
        if (previewDate) {
            previewDate.textContent = dateText;
        }

        // Status
        const isActive = ativaInput?.value === "true";
        if (previewStatus) {
            previewStatus.textContent = isActive ? "Ativo na TV" : "Inativo";
            previewStatus.classList.toggle("status-active", isActive);
        }
    }

    function formatDateForDisplay(dateStr) {
        if (!dateStr) return "";
        const date = new Date(dateStr + "T00:00:00");
        return date.toLocaleDateString("pt-BR", { month: "short", day: "numeric" });
    }

    // Listen to field changes
    if (segmentoSelect) {
        segmentoSelect.addEventListener("change", updatePreview);
    }
    if (tituloInput) {
        tituloInput.addEventListener("input", updatePreview);
    }
    if (descricaoTextarea) {
        descricaoTextarea.addEventListener("input", updatePreview);
    }
    if (dataInicioInput) {
        dataInicioInput.addEventListener("change", updatePreview);
    }
    if (dataFimInput) {
        dataFimInput.addEventListener("change", updatePreview);
    }

    updatePreview();
}

/* ================================================================
   Auto-scroll infinito para TV/dashboard
   ----------------------------------------------------------------
   Estratégia:
   1. Coleta todos os filhos diretos do container [data-tv-autoscroll].
   2. Clona-os e insere no final (marcados com data-clone-scroll).
   3. Scroll contínuo pixel a pixel; quando scrollTop atinge a
      metade (= altura real do conteúdo), reseta para 0 – o clone
      garante que não haja salto visual.
   4. Pausa no hover/focus; reinicia após saída do mouse.
   5. Só roda se conteúdo > container.
   ================================================================ */
function initTvInfiniteAutoScroll() {
    if (!isDashboardTvMode()) {
        return;
    }

    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    if (prefersReducedMotion) {
        return;
    }

    const DEFAULT_SPEED = 0.35;
    const INITIAL_DELAY_MS = 1800;
    const END_PAUSE_MS = 1200;
    const RESTART_DELAY_MS = 900;
    const OVERFLOW_THRESHOLD_PX = 12;

    const containers = Array.from(
        document.querySelectorAll("[data-tv-autoscroll], [data-auto-scroll]")
    );

    containers.forEach((container) => {
        setupStableScroller(container);
    });

    function setupStableScroller(scroller) {
        if (scroller.dataset.scrollerInit === "true") {
            return;
        }

        scroller.dataset.scrollerInit = "true";

        window.requestAnimationFrame(() => {
            const maxScroll = scroller.scrollHeight - scroller.clientHeight;

            if (maxScroll <= OVERFLOW_THRESHOLD_PX) {
                return;
            }

            const customSpeed = Number.parseFloat(scroller.dataset.autoScrollSpeed || "");
            const speed = Number.isFinite(customSpeed) && customSpeed > 0
                ? Math.min(customSpeed / 20, 0.65)
                : DEFAULT_SPEED;

            let rafId = null;
            let paused = false;
            let waiting = false;
            let restartTimeout = null;
            let lastTimestamp = null;

            const step = (timestamp) => {
                if (paused) {
                    rafId = null;
                    lastTimestamp = null;
                    return;
                }

                if (lastTimestamp === null) {
                    lastTimestamp = timestamp;
                }

                const delta = timestamp - lastTimestamp;
                lastTimestamp = timestamp;

                scroller.scrollTop += speed * (delta / 16.67);

                const currentMaxScroll = scroller.scrollHeight - scroller.clientHeight;

                if (scroller.scrollTop >= currentMaxScroll - 1 && !waiting) {
                    waiting = true;

                    window.setTimeout(() => {
                        scroller.scrollTo({
                            top: 0,
                            behavior: "smooth"
                        });

                        window.setTimeout(() => {
                            waiting = false;
                            lastTimestamp = null;
                            rafId = window.requestAnimationFrame(step);
                        }, 550);
                    }, END_PAUSE_MS);

                    rafId = null;
                    return;
                }

                rafId = window.requestAnimationFrame(step);
            };

            const start = () => {
                if (rafId !== null || waiting) {
                    return;
                }

                paused = false;
                rafId = window.requestAnimationFrame(step);
            };

            const stop = () => {
                paused = true;

                if (rafId !== null) {
                    window.cancelAnimationFrame(rafId);
                    rafId = null;
                }

                if (restartTimeout !== null) {
                    window.clearTimeout(restartTimeout);
                    restartTimeout = null;
                }

                lastTimestamp = null;
            };

            const scheduleRestart = () => {
                if (restartTimeout !== null) {
                    window.clearTimeout(restartTimeout);
                }

                restartTimeout = window.setTimeout(() => {
                    paused = false;
                    start();
                }, RESTART_DELAY_MS);
            };

            scroller.addEventListener("mouseenter", stop);
            scroller.addEventListener("focusin", stop);
            scroller.addEventListener("mouseleave", scheduleRestart);
            scroller.addEventListener("focusout", scheduleRestart);

            window.setTimeout(start, INITIAL_DELAY_MS);
        });
    }
}
