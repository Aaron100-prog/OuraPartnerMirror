package com.ourapartnermirror.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.browser.customtabs.CustomTabsIntent
import java.net.URLEncoder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private const val CLIENT_ID = "4a31261c-328e-4c44-b870-5728a78f2371"
        private const val REDIRECT_URI = "ourapartnermirror://oauth"
        private const val SCOPES = "personal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleOAuthCallback(intent)

        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 8.dp,
                                top = 12.dp,
                                bottom = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Oura Partner Mirror",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply { open() };
                                }

                            }
                        ) {
                            Text(
                                text = "⋮",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }

                    HorizontalDivider()

                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                Text("Drawer title", modifier = Modifier.padding(16.dp))
                                HorizontalDivider()
                                NavigationDrawerItem(
                                    label = { Text(text = "Drawer Item") },
                                    selected = false,
                                    onClick = { /*TODO*/ }
                                )
                                // ...other drawer items
                            }
                        }
                    ) {
                        // Screen content
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthCallback(intent)
    }

    private fun startOuraLogin() {

        val authUrl =
            "https://cloud.ouraring.com/oauth/authorize" +
                    "?response_type=token" +
                    "&client_id=${URLEncoder.encode(CLIENT_ID, "UTF-8")}" +
                    "&redirect_uri=${URLEncoder.encode(REDIRECT_URI, "UTF-8")}" +
                    "&scope=${URLEncoder.encode(SCOPES, "UTF-8")}"

        val customTabsIntent = CustomTabsIntent.Builder().build()

        customTabsIntent.launchUrl(
            this,
            Uri.parse(authUrl)
        )
    }

    private fun handleOAuthCallback(intent: Intent?) {

        val uri = intent?.data ?: return

        if (uri.scheme != "ourapartnermirror") {
            return
        }

        if (uri.host != "oauth") {
            return
        }

        val fragment = uri.fragment ?: return

        val values = fragment
            .split("&")
            .mapNotNull { parameter ->
                val parts = parameter.split("=", limit = 2)

                if (parts.size == 2) {
                    parts[0] to parts[1]
                } else {
                    null
                }
            }
            .toMap()

        val accessToken = values["access_token"]

        if (accessToken != null) {

            println("Oura access token received!")

            // Für den ersten Test zeigen wir nur an,
            // dass die Autorisierung funktioniert hat.
        }
    }
}
