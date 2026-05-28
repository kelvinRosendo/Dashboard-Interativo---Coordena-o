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

    const SCROLL_SPEED_PX = 1;           // pixels por tick
    const SCROLL_INTERVAL_MS = 50;       // ~20 fps — suave mas leve
    const INITIAL_DELAY_MS = 2200;       // espera antes de iniciar
    const RESTART_DELAY_MS = 1600;       // espera após mouseleave
    const OVERFLOW_THRESHOLD_PX = 12;    // margem mínima de overflow

    /** Encontra todos os containers marcados, sem duplicar */
    const containers = Array.from(
        document.querySelectorAll("[data-tv-autoscroll], [data-auto-scroll]")
    );

    containers.forEach((container) => {
        setupInfiniteScroller(container);
    });

    function setupInfiniteScroller(scroller) {
        // Verifica se já foi inicializado (segurança contra re-calls)
        if (scroller.dataset.scrollerInit === "true") {
            return;
        }
        scroller.dataset.scrollerInit = "true";

        // Coleta os filhos originais (ignora nós de texto vazios)
        const originalChildren = Array.from(scroller.children).filter(
            (child) => !child.hasAttribute("data-clone-scroll")
        );

        if (originalChildren.length === 0) {
            return;
        }

        // Aguarda render para medir dimensões corretas
        window.requestAnimationFrame(() => {
            const hasOverflow =
                scroller.scrollHeight > scroller.clientHeight + OVERFLOW_THRESHOLD_PX;

            if (!hasOverflow) {
                return; // conteúdo cabe sem scroll
            }

            // Cria clones para efeito contínuo
            const clones = originalChildren.map((child) => {
                const clone = child.cloneNode(true);
                clone.setAttribute("data-clone-scroll", "true");
                clone.setAttribute("aria-hidden", "true");
                return clone;
            });

            const fragment = document.createDocumentFragment();
            clones.forEach((clone) => fragment.appendChild(clone));
            scroller.appendChild(fragment);

            // Calcula a "metade" — ponto onde os clones começam
            // É a altura do conteúdo antes dos clones
            const realContentHeight = scroller.scrollHeight / 2;

            let rafId = null;
            let intervalId = null;
            let restartTimeout = null;
            let paused = false;

            const scrollTick = () => {
                if (paused) {
                    return;
                }

                scroller.scrollTop += SCROLL_SPEED_PX;

                // Quando chegou ou passou da metade, reseta de volta ao início.
                // Como o clone é idêntico, o visual não muda.
                if (scroller.scrollTop >= realContentHeight) {
                    scroller.scrollTop = scroller.scrollTop - realContentHeight;
                }
            };

            const startScroll = () => {
                if (intervalId !== null) {
                    return;
                }
                paused = false;
                intervalId = window.setInterval(scrollTick, SCROLL_INTERVAL_MS);
            };

            const stopScroll = () => {
                paused = true;
                if (intervalId !== null) {
                    window.clearInterval(intervalId);
                    intervalId = null;
                }
                if (restartTimeout !== null) {
                    window.clearTimeout(restartTimeout);
                    restartTimeout = null;
                }
            };

            const scheduleRestart = () => {
                if (restartTimeout !== null) {
                    window.clearTimeout(restartTimeout);
                }
                restartTimeout = window.setTimeout(startScroll, RESTART_DELAY_MS);
            };

            // Pausa ao hover / focus
            scroller.addEventListener("mouseenter", stopScroll);
            scroller.addEventListener("focusin", stopScroll);
            scroller.addEventListener("mouseleave", scheduleRestart);
            scroller.addEventListener("focusout", scheduleRestart);

            // Inicia após delay
            window.setTimeout(startScroll, INITIAL_DELAY_MS);
        });
    }
}
