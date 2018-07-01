package me.dmdev.rxpm.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class UntilDetectorTest {

    @Test fun usingSubscribeWithoutUntilInCreate() {
        lint()
            .files(
                kotlin(
                    """
                    |package foo
                    |import io.reactivex.Observable
                    |import me.dmdev.rxpm.PresentationModel
                    |class ExamplePm: PresentationModel() {
                    |    override fun onCreate() {
                    |        super.onCreate()
                    |        Observable.just(1)
                    |            .subscribe()
                    |    }
                    |}
                    """.trimMargin()
                )
            )
            .issues(UntilDetector.SUBSCRIBE_WITHOUT_UNTIL)
            .run()
            .expect(
                """
                |src/foo/ExamplePm.java:8: Warning: Call to `subscribe()` without a corresponding `untilUnbind()` or `untilDestroy()` [SubscribeWithoutUntil]
                |            .subscribe()
                |            ~~~~~~~~~~~~
                |0 errors, 1 warnings
                """.trimMargin()
            )
    }

}