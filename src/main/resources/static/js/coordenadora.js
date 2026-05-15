document.addEventListener("DOMContentLoaded", () => {
    const checkItems = Array.from(document.querySelectorAll(".js-check-item"));
    const progress = document.querySelector(".js-check-progress");

    const updateChecklist = () => {
        const total = checkItems.length;
        const done = checkItems.filter((item) => {
            const input = item.querySelector(".js-check-input");
            const checked = Boolean(input && input.checked);
            item.classList.toggle("coordenadora-check--done", checked);
            return checked;
        }).length;

        if (progress) {
            progress.textContent = `${done} de ${total} concluidas`;
        }
    };

    checkItems.forEach((item) => {
        const input = item.querySelector(".js-check-input");

        if (input) {
            input.addEventListener("change", updateChecklist);
        }
    });

    updateChecklist();

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
