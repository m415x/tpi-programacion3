import { renderHeader, renderAside, renderFooter } from "@utils/components";

const initClient = (): void => {
    renderHeader("main-header");
    renderAside("main-sidebar");
    renderFooter("main-footer");
};

document.addEventListener("DOMContentLoaded", initClient);
