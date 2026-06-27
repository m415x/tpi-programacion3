import { profileComponent } from "@utils/components";

export const clientProfileController = {
    async init(targetContainer: HTMLElement): Promise<void> {
        await profileComponent.render(targetContainer);
    },
};
