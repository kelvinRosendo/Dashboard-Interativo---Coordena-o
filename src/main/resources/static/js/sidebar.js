document.addEventListener("DOMContentLoaded", () => {
    const STORAGE_KEY = "dashboardSidebarCollapsed";
    const sidebar = document.querySelector(".app-sidebar");

    if (!sidebar) {
        return;
    }

    const shell = sidebar.closest(".app-shell");
    const toggle = sidebar.querySelector("[data-sidebar-toggle]");
    const toggleIcon = sidebar.querySelector("[data-sidebar-toggle-icon]");
    const savedState = window.localStorage.getItem(STORAGE_KEY);
    const shouldStartCollapsed = savedState === null
        ? Boolean(shell && shell.classList.contains("app-shell--tv")) || window.matchMedia("(max-width: 1100px)").matches
        : savedState === "true";

    const setCollapsed = (collapsed) => {
        sidebar.classList.toggle("app-sidebar--collapsed", collapsed);

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
    };

    setCollapsed(shouldStartCollapsed);

    if (toggle) {
        toggle.addEventListener("click", () => {
            const collapsed = !sidebar.classList.contains("app-sidebar--collapsed");
            setCollapsed(collapsed);
            window.localStorage.setItem(STORAGE_KEY, String(collapsed));
        });
    }
});
