import Packet from '#/io/Packet.js';

export default class Envelope {
    length: number = 2;
    shapeDelta: Int32Array = new Int32Array(2);
    shapePeak: Int32Array = new Int32Array(2);

    start: number = 0;
    end: number = 0;
    form: number = 0;
    threshold: number = 0;
    position: number = 0;
    delta: number = 0;
    amplitude: number = 0;
    ticks: number = 0;

    constructor() {
        this.shapeDelta[0] = 0;
        this.shapeDelta[1] = 65535;
        this.shapePeak[0] = 0;
        this.shapePeak[1] = 65535;
    }

    load(dat: Packet): void {
        this.form = dat.g1();
        this.start = dat.g4();
        this.end = dat.g4();

        this.length = dat.g1();
        this.shapeDelta = new Int32Array(this.length);
        this.shapePeak = new Int32Array(this.length);

        for (let i = 0; i < this.length; i++) {
            this.shapeDelta[i] = dat.g2();
            this.shapePeak[i] = dat.g2();
        }
    }

    genInit(): void {
        this.threshold = 0;
        this.position = 0;
        this.delta = 0;
        this.amplitude = 0;
        this.ticks = 0;
    }

    genNext(delta: number): number {
        if (this.ticks >= this.threshold) {
            this.amplitude = this.shapePeak[this.position++] << 15;

            if (this.position >= this.length) {
                this.position = this.length - 1;
            }

            this.threshold = ((this.shapeDelta[this.position] / 65536.0) * delta) | 0;
            if (this.threshold > this.ticks) {
                this.delta = (((this.shapePeak[this.position] << 15) - this.amplitude) / (this.threshold - this.ticks)) | 0;
            }
        }

        this.amplitude += this.delta;
        this.ticks++;
        return (this.amplitude - this.delta) >> 15;
    }
}
