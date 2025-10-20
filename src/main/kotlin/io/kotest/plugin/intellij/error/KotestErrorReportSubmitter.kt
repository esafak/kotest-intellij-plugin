package io.kotest.plugin.intellij.error

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.Consumer
import java.awt.Component
import java.net.URLEncoder

class KotestErrorReportSubmitter : ErrorReportSubmitter() {

   override fun getReportActionText(): String {
      return "Create Issue on GitHub"
   }

   override fun submit(
      events: Array<out IdeaLoggingEvent>,
      additionalInfo: String?,
      parentComponent: Component,
      consumer: Consumer<in SubmittedReportInfo>
   ): Boolean {
      val event = events.first()
      val throwable = event.throwable
      val stacktrace = throwable?.stackTraceToString() ?: event.message ?: ""

      val plugin = PluginManagerCore.getPlugin(PluginId.getId("io.kotest.plugin.intellij"))
      val pluginVersion = plugin?.version ?: "Unknown"

      val ideVersion = ApplicationInfo.getInstance().fullVersion

      val title = URLEncoder.encode("Plugin Error: ${event.message}", "UTF-8")
      val body = URLEncoder.encode(
         """
**Description of the bug**
A clear and concise description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected behavior**
A clear and concise description of what you expected to happen.

**Screenshots**
If applicable, add screenshots to help explain your problem.

**Stacktrace**
```
$stacktrace
```

**Versions**
 - Kotest plugin version: $pluginVersion
 - IDE version: $ideVersion
 - Operating System: ${System.getProperty("os.name")}

**Additional context**
Add any other context about the problem here.
$additionalInfo
""",
         "UTF-8"
      )

      BrowserUtil.browse("https://github.com/kotest/kotest-intellij-plugin/issues/new?title=$title&body=$body")
      consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
      return true
   }
}
