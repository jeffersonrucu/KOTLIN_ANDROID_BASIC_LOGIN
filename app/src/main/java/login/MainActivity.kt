    package login

    import android.os.Bundle
    import android.util.Log
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.defaultMinSize
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.safeDrawingPadding
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.ClickableText
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextField
    import androidx.compose.material3.TextFieldDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.shadow
    import androidx.compose.ui.focus.FocusRequester
    import androidx.compose.ui.focus.focusRequester
    import androidx.compose.ui.focus.onFocusChanged
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.AnnotatedString
    import androidx.compose.ui.text.TextStyle
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.example.loginstudiostg.R
    import com.google.firebase.Firebase
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.firestore
    import login.ui.theme.Black900
    import login.ui.theme.Blue100
    import login.ui.theme.Blue900
    import login.ui.theme.LoginStudioSTGTheme

    class MainActivity : ComponentActivity() {
        val db = Firebase.firestore

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                LoginStudioSTGTheme {
                    MyAppContent(db)
                }
            }
        }
    }

    @Composable
    fun MyAppContent(db: FirebaseFirestore) {
        val (email, setEmail) = remember { mutableStateOf("") }
        val (password, setPassword) = remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Logo()

                // Input de e-mail
                Box(modifier = Modifier.padding(35.dp, 95.dp, 35.dp, 15.dp)) {
                    Input(label = "E-mail", value = email, onValueChange = setEmail)
                }

                // Input de senha
                Box(modifier = Modifier.padding(35.dp, 15.dp)) {
                    Input(label = "Password", value = password, onValueChange = setPassword)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(35.dp, 15.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    ClickableText(
                        text = AnnotatedString("Forgot your password?"),
                        style = TextStyle(
                            color = Blue900,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        onClick = { /* Logic Here */ }
                    )
                }

                // Botão de Sign In
                Button(
                    modifier = Modifier
                        .padding(35.dp, 25.dp)
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 60.dp)
                        .shadow(10.dp, shape = RoundedCornerShape(10.dp), spotColor = Blue900),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue900,
                        disabledContentColor = Blue900,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        val user = hashMapOf(
                            "email" to email,
                            "password" to password
                        )

                        db.collection("users")
                            .add(user)
                            .addOnSuccessListener {
                                // Success
                            }
                            .addOnFailureListener {
                                // Error
                            }
                    }
                ) {
                    Text(text = "Sign In", style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    ))
                }

                Box(
                    modifier = Modifier.padding(35.dp, 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ClickableText(
                        text = AnnotatedString("Create new account"),
                        style = TextStyle(
                            color = Black900,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        onClick = {}
                    )
                }
            }
        }
    }

    @Composable
    fun Logo() {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Studio STG",
        )
    }

    @Composable
    fun Input(label: String, value: String, onValueChange: (String) -> Unit) {
        val focusRequester = remember { FocusRequester() }
        val isFocused = remember { mutableStateOf(false) }

        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            maxLines = 1,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(
                color = Black900,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused.value = focusState.isFocused
                }
                .border(
                    border = if (isFocused.value) BorderStroke(2.dp, Blue900) else BorderStroke(0.dp, Color.Transparent),
                    shape = RoundedCornerShape(10.dp),
                )
                .fillMaxWidth()
                .defaultMinSize(minHeight = 64.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Blue100,
                unfocusedContainerColor = Blue100,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun MyAppContentPreview() {
        val db = Firebase.firestore

        LoginStudioSTGTheme {
            MyAppContent(db)
        }
    }
