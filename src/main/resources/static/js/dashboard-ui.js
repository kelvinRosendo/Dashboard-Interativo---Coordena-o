const SIDEBAR_STORAGE_KEY = "dashboardSidebarCollapsed";

function resolveInitialSidebarCollapsed(shell) {
    try {
        const savedState = window.localStorage.getItem(SIDEBAR_STORAGE_KEY);
        const isTvShell = Boolean(shell && shell.classList.contains("app-shell--tv"));
        const isCompactViewport = window.matchMedia("(max-width: 1100px)").matches;
        return savedState === null
            ? isTvShell || isCompactViewport
            : savedState === "true";
    } catch {
        return false;
    }
}

function applySidebarBootstrap() {
    const sidebar = document.querySelector(".app-sidebar");
    const shell = document.querySelector(".app-shell");
    const collapsed = resolveInitialSidebarCollapsed(shell);

    document.documentElement.classList.toggle("sidebar-collapsed-pending", collapsed);

    if (shell) {
        shell.classList.toggle("app-shell--sidebar-collapsed", collapsed);
    }

    if (!sidebar) {
        return;
    }

    sidebar.classList.toggle("app-sidebar--collapsed", collapsed);

    const toggle = sidebar.querySelector("[data-sidebar-toggle]");
    const toggleIcon = sidebar.querySelector("[data-sidebar-toggle-icon]");

    if (toggle) {
        toggle.setAttribute("aria-expanded", String(!collapsed));
        toggle.setAttribute("aria-label", collapsed ? "Expandir menu" : "Recolher menu");
        toggle.setAttribute("title", collapsed ? "Expandir menu" : "Recolher menu");
    }

    if (toggleIcon) {
        toggleIcon.textContent = collapsed ? ">" : "<";
    }
}

applySidebarBootstrap();

document.addEventListener("DOMContentLoaded", () => {
    initSidebar();
    initConflictModal();
    initDashboardTimer();
});

function initSidebar() {
    const sidebar = document.querySelector(".app-sidebar");

    if (!sidebar) {
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

        if (toggle) {
            toggle.setAttribute("aria-expanded", String(!collapsed));
            toggle.setAttribute("aria-label", collapsed ? "Expandir menu" : "Recolher menu");
            toggle.setAttribute("title", collapsed ? "Expandir menu" : "Recolher menu");
        }

        if (toggleIcon) {
            toggleIcon.textContent = collapsed ? ">" : "<";
        }

        if (persist) {
            window.localStorage.setItem(SIDEBAR_STORAGE_KEY, String(collapsed));
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
    const target = timer.dataset.dashboardTarget || "/tv/calendario?modo=mensal";
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
            window.location.assign(target);
        }
    }, 1000);
}
