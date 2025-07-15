package org.mapstruct

import javax.tools.Diagnostic.Kind

enum class ReportingPolicy {
    /**
     * No report will be created for the given issue.
     */
    IGNORE,

    /**
     * A report with [Kind.WARNING] will be created for the given issue.
     */
    WARN,

    /**
     * A report with [Kind.ERROR] will be created for the given issue,
     * causing the compilation to fail.
     */
    ERROR
}
