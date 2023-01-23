package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.dao.UserService.updateUser
import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Image.Companion.fromEntity
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ImageService {

    fun get(imageID: UUID) = transaction {
        return@transaction ImageEntity.findById(imageID)?.fromEntity()
    }

    fun new(image: Image, user: User) = transaction {
        updateUser(user)
        return@transaction ImageEntity.new { update(image) }.fromEntity()
    }

    fun update(image: Image, user: User) = transaction {
        updateUser(user)
        return@transaction ImageEntity.findById(image.globalId)?.apply { update(image) }?.fromEntity()
    }

    fun delete(id: UUID) = transaction { deleteImage(id) }

    private fun Transaction.deleteImage(id: UUID) {
        ImageEntity.findById(id)?.delete()
    }

    fun Transaction.cleanImage(id: UUID) {
        val count = ImageTable
            .join(PhraseTable, JoinType.LEFT, ImageTable.id, PhraseTable.idImage)
            .join(CardTable, JoinType.LEFT, ImageTable.id, CardTable.idImage)
            .select((PhraseTable.idImage eq id) or (CardTable.idImage eq id))
            .count()
        if (count == 0L) this.deleteImage(id)
    }

}