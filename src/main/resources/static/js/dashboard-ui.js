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
    initWeekFocusCollapsible();
    initTvInfiniteAutoScroll();
    initMobileSidebar();
    initFormLoadingStates();
    initSidebarKeyboard();
});

function initSidebar() {
    const sidebar = document.querySelector(".app-sidebar");

    if (!sidebar || isDashboardTvMode()) {
        document.documentElement.classList.remove("sidebar-collapsed-pending");
        return;
    }

    const shell = sidebar.closest(".app-shell");
    const toggleButton = sidebar.querySelector("[data-sidebar-toggle]");
    const toggleIcon = sidebar.querySelector("[data-sidebar-toggle-icon]");
    const toggleTriggers = sidebar.querySelectorAll("[data-sidebar-toggle]");
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

        updateSidebarToggle(toggleButton, toggleIcon, collapsed);

        if (persist) {
            try {
                window.localStorage.setItem(SIDEBAR_STORAGE_KEY, String(collapsed));
            } catch {}
        }
    };

    setCollapsed(resolveInitialSidebarCollapsed(shell), false);
    document.documentElement.classList.remove("sidebar-collapsed-pending");

    toggleTriggers.forEach((trigger) => {
        trigger.addEventListener("click", () => {
            const collapsed = !sidebar.classList.contains("app-sidebar--collapsed");
            setCollapsed(collapsed);
        });
    });

    if (!prefersReducedMotion) {
        window.requestAnimationFrame(() => {
            sidebar.classList.add("app-sidebar--ready");
            if (shell) {
                shell.classList.add("app-shell--sidebar-ready");
            }
        });
    }
}

function initWeekFocusCollapsible() {
    const collapsibles = document.querySelectorAll(".week-focus-collapsible__details");
    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    if (!collapsibles.length) {
        return;
    }

    collapsibles.forEach((details) => {
        const summary = details.querySelector(".week-focus-collapsible__summary");
        const content = details.querySelector(".week-focus-collapsible__content");

        if (!summary || !content) {
            return;
        }

        const setExpanded = (expanded) => {
            summary.setAttribute("aria-expanded", String(expanded));
            details.classList.toggle("is-collapsed", !expanded);
        };

        const finishAnimation = (expanded) => {
            details.classList.remove("is-animating");
            content.style.maxHeight = "";
            content.style.opacity = "";
            content.style.transform = "";

            if (!expanded) {
                details.removeAttribute("open");
            }
        };

        const animate = (expanded) => {
            if (prefersReducedMotion) {
                details.toggleAttribute("open", expanded);
                setExpanded(expanded);
                return;
            }

            details.classList.add("is-animating");

            if (expanded) {
                details.setAttribute("open", "");
                setExpanded(true);
                content.style.maxHeight = "0px";
                content.style.opacity = "0";
                content.style.transform = "translateY(-6px)";

                window.requestAnimationFrame(() => {
                    content.style.maxHeight = `${content.scrollHeight}px`;
                    content.style.opacity = "1";
                    content.style.transform = "translateY(0)";
                });
            } else {
                content.style.maxHeight = `${content.scrollHeight}px`;
                content.style.opacity = "1";
                content.style.transform = "translateY(0)";

                window.requestAnimationFrame(() => {
                    setExpanded(false);
                    content.style.maxHeight = "0px";
                    content.style.opacity = "0";
                    content.style.transform = "translateY(-6px)";
                });
            }

            const fallbackTimer = window.setTimeout(() => {
                content.removeEventListener("transitionend", onEnd);
                finishAnimation(expanded);
            }, 420);

            const onEnd = (event) => {
                if (event.target !== content || event.propertyName !== "max-height") {
                    return;
                }

                window.clearTimeout(fallbackTimer);
                content.removeEventListener("transitionend", onEnd);
                finishAnimation(expanded);
            };

            content.addEventListener("transitionend", onEnd);
        };

        setExpanded(details.hasAttribute("open"));

        summary.addEventListener("click", (event) => {
            event.preventDefault();

            if (details.classList.contains("is-animating")) {
                return;
            }

            animate(!details.hasAttribute("open"));
        });
    });
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

    const DURATION_MS = 30000;
    const OVERFLOW_THRESHOLD_PX = 12;
    const INITIAL_DELAY_MS = 1000;
    const PAUSE_AT_END_MS = 800;

    Array.from(document.querySelectorAll("[data-tv-autoscroll]")).forEach((scroller) => {
        if (scroller.dataset.autoScrollInit === "true") {
            return;
        }

        scroller.dataset.autoScrollInit = "true";

        window.requestAnimationFrame(() => {
            const maxScroll = scroller.scrollHeight - scroller.clientHeight;
            if (maxScroll <= OVERFLOW_THRESHOLD_PX) {
                return;
            }

            const speed = maxScroll / DURATION_MS;
            let animId = null;
            let isPaused = false;
            let startTime = null;

            const scroll = (timestamp) => {
                if (isPaused) {
                    animId = null;
                    startTime = null;
                    return;
                }

                if (startTime === null) {
                    startTime = timestamp;
                }

                const elapsed = timestamp - startTime;
                const newScrollTop = speed * elapsed;

                if (newScrollTop >= maxScroll) {
                    scroller.scrollTop = maxScroll;
                    window.setTimeout(() => {
                        scroller.scrollTop = 0;
                        startTime = null;
                        if (!isPaused) {
                            animId = window.requestAnimationFrame(scroll);
                        }
                    }, PAUSE_AT_END_MS);
                    animId = null;
                    return;
                }

                scroller.scrollTop = newScrollTop;
                animId = window.requestAnimationFrame(scroll);
            };

            const startScroll = () => {
                if (animId !== null) {
                    return;
                }
                isPaused = false;
                startTime = null;
                animId = window.requestAnimationFrame(scroll);
            };

            const stopScroll = () => {
                isPaused = true;
                if (animId !== null) {
                    window.cancelAnimationFrame(animId);
                    animId = null;
                }
                startTime = null;
            };

            const resumeScroll = () => {
                stopScroll();
                startScroll();
            };

            scroller.addEventListener("mouseenter", stopScroll);
            scroller.addEventListener("focusin", stopScroll);
            scroller.addEventListener("mouseleave", resumeScroll);
            scroller.addEventListener("focusout", resumeScroll);

            window.setTimeout(startScroll, INITIAL_DELAY_MS);
        });
    });
}

function initMobileSidebar() {
    const hamburger = document.querySelector("[data-sidebar-hamburger]");
    const overlay = document.querySelector("[data-sidebar-overlay]");
    const sidebar = document.querySelector(".app-sidebar");

    if (!hamburger || !sidebar) return;

    const open = () => {
        sidebar.classList.add("app-sidebar--mobile-open");
        if (overlay) overlay.classList.add("sidebar-overlay--visible");
        document.body.style.overflow = "hidden";
    };

    const close = () => {
        sidebar.classList.remove("app-sidebar--mobile-open");
        if (overlay) overlay.classList.remove("sidebar-overlay--visible");
        document.body.style.overflow = "";
    };

    hamburger.addEventListener("click", open);
    if (overlay) overlay.addEventListener("click", close);

    sidebar.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", close);
    });
}

function initFormLoadingStates() {
    document.addEventListener("submit", (event) => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)) return;

        const submitBtn = form.querySelector("button[type='submit'], input[type='submit']");
        if (!submitBtn || submitBtn.disabled) return;

        submitBtn.disabled = true;
        submitBtn.setAttribute("data-loading-original", submitBtn.textContent || submitBtn.value || "");

        if (submitBtn.tagName === "BUTTON") {
            submitBtn.innerHTML = '<span class="btn-spinner"></span> Enviando...';
        } else {
            submitBtn.value = "Enviando...";
        }

        submitBtn.classList.add("btn--loading");
    });

    document.addEventListener("reset", (event) => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)) return;

        form.querySelectorAll(".btn--loading").forEach((btn) => {
            const original = btn.getAttribute("data-loading-original");
            btn.disabled = false;
            btn.classList.remove("btn--loading");
            if (btn.tagName === "BUTTON" && original !== null) {
                btn.textContent = original;
            } else if (original !== null) {
                btn.value = original;
            }
            btn.removeAttribute("data-loading-original");
        });
    });
}

function initSidebarKeyboard() {
    const toggle = document.querySelector("[data-sidebar-toggle]");
    if (!toggle) return;

    toggle.addEventListener("keydown", (event) => {
        if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            toggle.click();
        }
    });
}
