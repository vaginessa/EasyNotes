package com.kin.easynotes.presentation.components.Makrdown

class MarkdownBuilder(internal val lines: List<String>, private var lineProcessors: List<MarkdownLineProcessor>) {
    var lineIndex = -1

    internal val content = mutableListOf<MarkdownElement>()

    init {
        lineProcessors += ImageInsertionProcessor()
    }
    fun add(element: MarkdownElement) {
        content.add(element)
    }

    fun parse() {
        while (hasNextLine()) {
            val line = nextLine()
            val processor = lineProcessors.find { it.canProcessLine(line) }
            if (processor != null) {
                processor.processLine(line, this)
            } else {
                add(NormalText(line))
            }
        }
    }

    private fun hasNextLine(): Boolean = lineIndex + 1 < lines.size

    private fun nextLine(): String {
        lineIndex++
        return lines[lineIndex]
    }
}