package login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginstudiostg.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    var currentScreen by remember { mutableStateOf("list") }
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var selectedUserData by remember { mutableStateOf<Map<String, Any>?>(null) }

    when (currentScreen) {
        "login" -> {
            Column {
                Login(db)
                Button(
                    onClick = { currentScreen = "list" },
                    modifier = Modifier
                        .padding(35.dp, 0.dp)
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 60.dp)
                        .shadow(10.dp, shape = RoundedCornerShape(10.dp), spotColor = Blue900),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue900),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        "Ver Usuários",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        }
        "list" -> {
            UsersListScreen(
                db = db,
                onEditUser = { id, userData ->
                    selectedUserId = id
                    selectedUserData = userData
                    currentScreen = "edit"
                },
                onBack = { currentScreen = "login" }
            )
        }
        "edit" -> {
            selectedUserId?.let { id ->
                selectedUserData?.let { userData ->
                    EditUserScreen(
                        db = db,
                        userId = id,
                        user = userData,
                        onBack = { currentScreen = "list" }
                    )
                }
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
fun Login(db: FirebaseFirestore) {
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val context = LocalContext.current

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

            Box(modifier = Modifier.padding(35.dp, 95.dp, 35.dp, 15.dp)) {
                Input(label = "E-mail", value = email, onValueChange = setEmail)
            }

            Box(modifier = Modifier.padding(35.dp, 15.dp)) {
                Input(label = "Password", value = password, onValueChange = setPassword)
            }

            Button(
                modifier = Modifier
                    .padding(35.dp, 25.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 60.dp)
                    .shadow(10.dp, shape = RoundedCornerShape(10.dp), spotColor = Blue900),
                colors = ButtonDefaults.buttonColors(containerColor = Blue900),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val user = hashMapOf(
                        "email" to email,
                        "password" to password
                    )

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                            setEmail("")
                            setPassword("")
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Erro ao cadastrar: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            ) {
                Text(
                    text = "Cadastrar",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                )
            }
        }
    }
}

@Composable
fun EditUserScreen(db: FirebaseFirestore, userId: String, user: Map<String, Any>, onBack: () -> Unit) {
    val (email, setEmail) = remember { mutableStateOf(user["email"].toString()) }
    val (password, setPassword) = remember { mutableStateOf(user["password"].toString()) }
    val context = LocalContext.current

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

            Box(modifier = Modifier.padding(35.dp, 95.dp, 35.dp, 15.dp)) {
                Input(label = "E-mail", value = email, onValueChange = setEmail)
            }

            Box(modifier = Modifier.padding(35.dp, 15.dp)) {
                Input(label = "Password", value = password, onValueChange = setPassword)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(35.dp, 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 60.dp)
                        .shadow(10.dp, shape = RoundedCornerShape(10.dp), spotColor = Blue900),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue900),
                    shape = RoundedCornerShape(10.dp),
                    onClick = onBack
                ) {
                    Text("Voltar", color = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 60.dp)
                        .shadow(10.dp, shape = RoundedCornerShape(10.dp), spotColor = Blue900),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue900),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        db.collection("users").document(userId)
                            .update(
                                mapOf(
                                    "email" to email,
                                    "password" to password
                                )
                            )
                            .addOnSuccessListener {
                                Toast.makeText(context, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Erro ao atualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                ) {
                    Text("Atualizar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun UsersListScreen(db: FirebaseFirestore, onEditUser: (String, Map<String, Any>) -> Unit, onBack: () -> Unit) {
    var users = remember { mutableStateListOf<Pair<String, Map<String, Any>>>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                users.clear()
                for (document in result) {
                    users.add(Pair(document.id, document.data))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Lista de Usuários",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue900
                    )
                )

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue900)
                ) {
                    Text("Voltar", color = Color.White)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(users.size) { index ->
                    val (id, userData) = users[index]
                    UserItem(
                        userData = userData,
                        onEdit = { onEditUser(id, userData) },
                        onDelete = {
                            db.collection("users").document(id)
                                .delete()
                                .addOnSuccessListener {
                                    users.removeAt(index)
                                    Toast.makeText(context, "Usuário deletado com sucesso!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Erro ao deletar: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItem(
    userData: Map<String, Any>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(Blue100, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Blue900.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp)
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Blue900
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Email: ${userData["email"]}",
                        style = TextStyle(
                            color = Black900,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "Senha: ${userData["password"]}",
                        style = TextStyle(
                            color = Black900,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue900,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .shadow(4.dp, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            "Editar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red.copy(alpha = 0.9f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .shadow(4.dp, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            "Deletar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
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