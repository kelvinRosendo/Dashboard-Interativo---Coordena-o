(function initSidebarStateBootstrap() {
    const STORAGE_KEY = "dashboardSidebarCollapsed";

    try {
        const savedState = window.localStorage.getItem(STORAGE_KEY);
        const shell = document.querySelector(".app-shell");
        const isTvShell = Boolean(shell && shell.classList.contains("app-shell--tv"));
        const isCompactViewport = window.matchMedia("(max-width: 1100px)").matches;
        const shouldCollapse = savedState === null
            ? isTvShell || isCompactViewport
            : savedState === "true";

        if (shouldCollapse) {
            document.documentElement.classList.add("sidebar-collapsed-pending");
        }
    } catch {
        // Ignora falhas de storage e segue com o estado padrao.
    }
})();

document.addEventListener("DOMContentLoaded", () => {
    initSidebar();
    initConflictModal();
    initDashboardTimer();
});

function initSidebar() {
    const STORAGE_KEY = "dashboardSidebarCollapsed";
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
            toggleIcon.textContent = collapsed ? "»" : "«";
        }

        if (persist) {
            window.localStorage.setItem(STORAGE_KEY, String(collapsed));
        }
    };

    let savedState = null;
    try {
        savedState = window.localStorage.getItem(STORAGE_KEY);
    } catch {
        savedState = null;
    }

    const shouldStartCollapsed = savedState === null
        ? Boolean(shell && shell.classList.contains("app-shell--tv")) || window.matchMedia("(max-width: 1100px)").matches
        : savedState === "true";

    setCollapsed(shouldStartCollapsed, false);
    document.documentElement.classList.remove("sidebar-collapsed-pending");

    if (toggle) {
        toggle.addEventListener("click", () => {
            const collapsed = !sidebar.classList.contains("app-sidebar--collapsed");
            setCollapsed(collapsed);
        });
    }

    if (!prefersReducedMotion) {
        sidebar.classList.add("app-sidebar--ready");
        if (shell) {
            shell.classList.add("app-shell--sidebar-ready");
        }
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
