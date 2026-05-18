document.addEventListener("DOMContentLoaded", () => {
    const checkItems = Array.from(document.querySelectorAll(".js-check-item"));
    const progress = document.querySelector(".js-check-progress");
    const progressPercent = document.querySelector(".js-check-percent");
    const progressPercentInline = document.querySelector(".js-check-percent-inline");
    const progressFill = document.querySelector(".js-check-progress-fill");
    const checklistStorageKey = `dashboardCoordChecklist:${window.location.pathname}`;

    const loadSavedChecklist = () => {
        try {
            return JSON.parse(window.localStorage.getItem(checklistStorageKey) || "{}");
        } catch {
            return {};
        }
    };

    const savedChecklist = loadSavedChecklist();

    const saveChecklist = () => {
        const state = {};

        checkItems.forEach((item, index) => {
            const input = item.querySelector(".js-check-input");
            const id = item.dataset.checkId || String(index);

            if (input) {
                state[id] = input.checked;
            }
        });

        window.localStorage.setItem(checklistStorageKey, JSON.stringify(state));
    };

    const updateChecklist = () => {
        const total = checkItems.length;
        const done = checkItems.filter((item) => {
            const input = item.querySelector(".js-check-input");
            const checked = Boolean(input && input.checked);
            item.classList.toggle("coordenadora-check--done", checked);
            item.classList.toggle("coord-checklist__item--done", checked);
            return checked;
        }).length;
        const percent = total > 0 ? Math.round((done / total) * 100) : 0;

        if (progress) {
            progress.textContent = `${done} de ${total} atividades concluidas`;
        }

        if (progressPercent) {
            progressPercent.textContent = `${percent}% concluido`;
        }

        if (progressPercentInline) {
            progressPercentInline.textContent = `${percent}%`;
        }

        if (progressFill) {
            progressFill.style.width = `${percent}%`;
        }
    };

    checkItems.forEach((item, index) => {
        const input = item.querySelector(".js-check-input");
        const id = item.dataset.checkId || String(index);

        if (input) {
            if (Object.prototype.hasOwnProperty.call(savedChecklist, id)) {
                input.checked = Boolean(savedChecklist[id]);
            }

            input.addEventListener("change", () => {
                updateChecklist();
                saveChecklist();
            });
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
