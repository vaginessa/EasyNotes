package com.kin.easynotes.presentation.components.Makrdown

sealed interface MarkdownElement {
    fun render(builder: StringBuilder)
}

data class Heading(val level: Int, val text: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("#".repeat(level)).append(" $text\n\n")
    }
}

data class CheckboxItem(val text: String, var checked: Boolean = false, var index: Int) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("[${if (checked) "X" else " "}] $text\n")
    }
}

data class Quote(val level: Int, val text: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("> ${text}\n")
    }
}

data class ImageInsertion(val photoUri: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("!($photoUri)\n\n")
    }
}


data class ListItem(val text: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("- ${text}\n")
    }
}

data class CodeBlock(val code: String, val iSEnded: Boolean = false) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("```")
        iSEnded.let {
            builder.append(it)
        }
        builder.append("\n$code\n```\n")
    }
}

data class NormalText(val text: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n\n")
    }
}