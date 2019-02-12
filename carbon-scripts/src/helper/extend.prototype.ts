Array.prototype.flatMap = function (f: any) {
    return [].concat.apply([], this.map(f))
}

Array.prototype.unique = function () {
    return this.filter((v, i) => this.indexOf(v) === i)
}

String.prototype.formatKt = function () {
    return this
        .replace(/^\s+$/gm, '')
}

String.prototype.indent = function (tab: number) {
    return this
        .replace(/^(.+)$/gm, ' '.repeat(tab) + '$1')
}