package com.example.chat.Repo;

import com.example.chat.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m WHERE (m.senderName = :sender1 OR m.senderName = :sender2) AND (m.receiverName = :receiver1 OR m.receiverName = :receiver2)")
    List<MessageEntity> findBySenderNameAndReceiverName(String sender1, String sender2, String receiver1, String receiver2);

    @Query("SELECT m.senderName AS other_user FROM MessageEntity m WHERE m.receiverName = :name UNION SELECT m.receiverName AS other_user FROM MessageEntity m WHERE m.senderName = :name")
    List<String> findAllConversations(String name);

    List<MessageEntity> findBySenderName(String senderName);
}