import Packet from '#/io/Packet.js';

export default class InputTracking {
    static active: boolean = false;
    static old: Packet | null = null;
    static out: Packet | null = null;
    static lastTime: number = 0;
    static trackedCount: number = 0;
    static lastMoveTime: number = 0;
    static lastX: number = 0;
    static lastY: number = 0;

    static activate(): void {
        this.old = Packet.alloc(1);
        this.out = null;
        this.lastTime = performance.now();
        this.active = true;
    }

    static deactivate(): void {
        this.active = false;
        this.old = null;
        this.out = null;
    }

    static flush(): Packet | null {
        let buffer: Packet | null = null;

        if (this.out && this.active) {
            buffer = this.out;
        }

        this.out = null;
        return buffer;
    }

    static stop(): Packet | null {
        let buffer: Packet | null = null;

        if (this.old && this.old.pos > 0 && this.active) {
            buffer = this.old;
        }

        this.deactivate();
        return buffer;
    }

    private static ensureCapacity(n: number): void {
        if (!this.old) {
            return;
        }

        if (this.old.pos + n >= 500) {
            const buffer: Packet = this.old;
            this.old = Packet.alloc(1);
            this.out = buffer;
        }
    }

    static mousePressed(x: number, y: number, button: number, _pointerType: string): void {
        if (!this.old) {
            return;
        }

        if (!this.active && (x >= 0 && x < 789 && y >= 0 && y < 532)) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;
        this.ensureCapacity(5);

        if (button === 2) {
            this.old.p1(1);
        } else {
            this.old.p1(2);
        }

        this.old.p1(delta);
        this.old.p3(x + (y << 10));
    }

    static mouseReleased(button: number, _pointerType: string): void {
        if (!this.old) {
            return;
        }

        if (!this.active) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;
        this.ensureCapacity(2);

        if (button === 2) {
            this.old.p1(3);
        } else {
            this.old.p1(4);
        }

        this.old.p1(delta);
    }

    static mouseMoved(x: number, y: number, _pointerType: string): void {
        if (!this.old) {
            return;
        }

        if (!this.active && (x >= 0 && x < 789 && y >= 0 && y < 532)) {
            return;
        }

        const now: number = performance.now();
        if (now - this.lastMoveTime < 50) {
            return;
        }

        this.lastMoveTime = now;
        this.trackedCount++;

        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;

        if (x - this.lastX < 8 && x - this.lastX >= -8 && y - this.lastY < 8 && y - this.lastY >= -8) {
            this.ensureCapacity(3);
            this.old.p1(5);
            this.old.p1(delta);
            this.old.p1(x + ((y - this.lastY + 8) << 4) + 8 - this.lastX);
        } else if (x - this.lastX < 128 && x - this.lastX >= -128 && y - this.lastY < 128 && y - this.lastY >= -128) {
            this.ensureCapacity(4);
            this.old.p1(6);
            this.old.p1(delta);
            this.old.p1(x + 128 - this.lastX);
            this.old.p1(y + 128 - this.lastY);
        } else {
            this.ensureCapacity(5);
            this.old.p1(7);
            this.old.p1(delta);
            this.old.p3(x + (y << 10));
        }

        this.lastX = x;
        this.lastY = y;
    }

    static keyPressed(key: number): void {
        if (!this.old) {
            return;
        }

        if (!this.active) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;

        if (key === 1000) {
            key = 11;
        } else if (key === 1001) {
            key = 12;
        } else if (key === 1002) {
            key = 14;
        } else if (key === 1003) {
            key = 15;
        } else if (key >= 1008) {
            key -= 992;
        }

        this.ensureCapacity(3);
        this.old.p1(8);
        this.old.p1(delta);
        this.old.p1(key);
    }

    static keyReleased(key: number): void {
        if (!this.old) {
            return;
        }

        if (!this.active) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;

        if (key === 1000) {
            key = 11;
        } else if (key === 1001) {
            key = 12;
        } else if (key === 1002) {
            key = 14;
        } else if (key === 1003) {
            key = 15;
        } else if (key >= 1008) {
            key -= 992;
        }

        this.ensureCapacity(3);
        this.old.p1(9);
        this.old.p1(delta);
        this.old.p1(key);
    }

    static focusGained(): void {
        if (!this.old) {
            return;
        }

        if (!this.active) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;

        this.ensureCapacity(2);
        this.old.p1(10);
        this.old.p1(delta);
    }

    static focusLost(): void {
        if (!this.old) {
            return;
        }

        if (!this.active) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;

        this.ensureCapacity(2);
        this.old.p1(11);
        this.old.p1(delta);
    }

    static mouseEntered(): void {
        if (!this.old) {
            return;
        }

        if (!this.active) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;

        this.ensureCapacity(2);
        this.old.p1(12);
        this.old.p1(delta);
    }

    static mouseExited(): void {
        if (!this.old) {
            return;
        }

        if (!this.active) {
            return;
        }

        this.trackedCount++;

        const now: number = performance.now();
        let delta: number = ((now - this.lastTime) / 10) | 0;
        if (delta > 250) {
            delta = 250;
        }

        this.lastTime = now;

        this.ensureCapacity(2);
        this.old.p1(13);
        this.old.p1(delta);
    }
}
