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
