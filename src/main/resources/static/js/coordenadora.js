document.addEventListener("DOMContentLoaded", () => {
    const dock = document.querySelector("[data-operation-dock]");
    const toggle = document.querySelector("[data-operation-toggle]");

    if (!dock || !toggle) {
        return;
    }

    const setDockState = (collapsed) => {
        dock.classList.toggle("operation-dock--collapsed", collapsed);
        toggle.setAttribute("aria-expanded", String(!collapsed));
        toggle.textContent = collapsed ? "Expandir" : "Recolher";
    };

    toggle.addEventListener("click", () => {
        setDockState(!dock.classList.contains("operation-dock--collapsed"));
    });

    setDockState(dock.classList.contains("operation-dock--collapsed"));
});
