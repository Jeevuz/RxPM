package me.dmdev.rxpm.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API


class RxPmIssueRegistry : IssueRegistry() {
    override val issues = listOf(UntilDetector.SUBSCRIBE_WITHOUT_UNTIL)
    override val api = CURRENT_API
}