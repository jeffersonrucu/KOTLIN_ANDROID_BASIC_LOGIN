package login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import login.database.entity.User
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
    Column {
//        Login(db)
        ListUsers(db)
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
                border = if (isFocused.value) BorderStroke(2.dp, Blue900) else BorderStroke(
                    0.dp,
                    Color.Transparent
                ),
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

@Composable
fun ListUsers(db: FirebaseFirestore) {
    var users = remember { mutableStateListOf<Map<String, Any>>() }

    db.collection("users")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                users.add(document.data)
            }
        }
        .addOnFailureListener { exception ->
            Log.w("Firestore", "Error getting documents.", exception)
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(10.dp),
    ) {
        Column {
            LazyColumn {
                items(users.size) {
                    if (users[it]["password"].toString() == "") {
                        return@items
                    }

                    Box(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .background(Blue900)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box() {
                                Text(
                                    text = "E-mail:",
                                    modifier = Modifier
                                        .padding(bottom = 8.dp),
                                    style = TextStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    users[it]["email"].toString(),
                                    Modifier.padding(start = 60.dp),
                                    style = TextStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            Box() {
                                Text(
                                    text = "Password:",
                                    modifier = Modifier
                                        .padding(bottom = 8.dp),
                                    style = TextStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    users[it]["password"].toString(),
                                    Modifier.padding(start = 85.dp),
                                    style = TextStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Login(db : FirebaseFirestore) {
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
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error adding document", e)
                        }
                }
            ) {
                Text(text = "Cadastrar", style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                ))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppContentPreview() {
    val db = Firebase.firestore

    LoginStudioSTGTheme {
        MyAppContent(db)
    }
}
