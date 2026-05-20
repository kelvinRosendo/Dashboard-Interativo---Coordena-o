document.addEventListener("DOMContentLoaded", () => {
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
});
