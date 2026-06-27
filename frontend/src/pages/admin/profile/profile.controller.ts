import { profileComponent } from "@utils/components";

export const adminProfileController = {
    async init(targetContainer: HTMLElement): Promise<void> {
        await profileComponent.render(targetContainer);
    },
};
