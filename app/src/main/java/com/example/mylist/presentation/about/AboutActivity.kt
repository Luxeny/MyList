package com.example.mylist.presentation.about

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mylist.BuildConfig
import com.example.mylist.R
import com.example.mylist.ui.theme.MyListTheme

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val titleFromXml = findViewById<TextView>(R.id.aboutTitle).text.toString()
        val subtitleFromXml = getString(R.string.about_subtitle)

        findViewById<ImageButton>(R.id.aboutBackButton).setOnClickListener { finish() }

        findViewById<ComposeView>(R.id.composeView).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyListTheme {
                    AboutContent(
                        xmlTitle = titleFromXml,
                        xmlSubtitle = subtitleFromXml,
                        versionName = BuildConfig.VERSION_NAME,
                        apiUrl = BuildConfig.API_URL,
                        isFullVersion = BuildConfig.FULL_VERSION,
                        flavorName = getString(R.string.app_name)
                    )
                }
            }
        }
    }
}

@Composable
fun AboutContent(
    xmlTitle: String,
    xmlSubtitle: String,
    versionName: String,
    apiUrl: String,
    isFullVersion: Boolean,
    flavorName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = Color.White.copy(alpha = 0.05f),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = xmlTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = xmlSubtitle,
                    color = Color.White.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Персональный менеджер контента для тех, кто ценит структуру и стиль. " +
                        "Храните всё, что хотите посмотреть или прочитать, в одном месте.",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Surface(
            color = Color.White.copy(alpha = 0.03f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(label = "Сборка", value = flavorName)
                InfoRow(label = "Версия", value = versionName)
                InfoRow(label = "API", value = apiUrl)
                InfoRow(
                    label = "Расширенные функции",
                    value = if (isFullVersion) "Включены" else "Отключены"
                )
                InfoRow(label = "Хранилище", value = "Local (Room)")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "© 2026 MyList",
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
