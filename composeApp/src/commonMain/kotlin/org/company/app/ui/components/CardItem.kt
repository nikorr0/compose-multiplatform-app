package org.company.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.company.app.Config
import org.company.app.model.CardDto

@Composable
fun CardItem(card: CardDto, onOpen: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    val imageUrl = "${Config.host}/storage/${card.imageUrl}"

    ElevatedCard(Modifier.fillMaxWidth()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp),
        )
        Column(Modifier.padding(12.dp)) {
            Text(card.name, style = MaterialTheme.typography.titleMedium)
            Text(card.shortText, style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.padding(top = 8.dp)) {
                Button(onClick = onOpen) { Text("View") }
                Spacer(Modifier.width(8.dp))
//                OutlinedButton(onClick = onEdit) { Text("Edit") }
//                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}