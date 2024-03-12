package com.employee.employeeandworkordermanagement.service;

import com.employee.employeeandworkordermanagement.entity.Message;
import com.employee.employeeandworkordermanagement.entity.Task;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public List<Message> getMessagesForUser(User user) {
        return messageRepository.findAllByReceiver(user);
    }

    public void notifyDesignerIfAssignedToTask(User designer, User sender, Task task) {
        Message message = new Message();
        message.setTitle("Task assignment.");
        message.setContent("You are assigned to task " + task.getTaskName() + ".");
        message.setReceiver(designer);
        message.setSender(sender);
        messageRepository.save(message);
    }

    public void notifyDesignerIfTaskIsEdited(User designer, User sender, Task task) {
        Message message = new Message();
        message.setTitle("Changes in " + task.getTaskName() + ".");
        message.setContent(task.getTaskName() + " has been edited");
        message.setReceiver(designer);
        message.setSender(sender);
        messageRepository.save(message);
    }

    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }

    public Message findById(Long id) {
        return messageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Message has not been found."));
    }

    public void notifyChangeDesignerInTask(User previousUser, User currentUser, User sender, Task task) {
        Message messageToPrevious = new Message();
        messageToPrevious.setTitle("Task assignment.");
        messageToPrevious.setContent("You are not assigned to task " + task.getTaskName() + " anymore.");
        messageToPrevious.setSender(sender);
        messageToPrevious.setReceiver(previousUser);
        messageRepository.save(messageToPrevious);
        Message messageToCurrent = new Message();
        messageToCurrent.setTitle("Task assignment.");
        messageToCurrent.setReceiver(currentUser);
        messageToCurrent.setSender(sender);
        messageToCurrent.setContent("You are now assigned to task " + task.getTaskName() + ".");
        messageRepository.save(messageToCurrent);
    }

    public void notifyOperatorThatTaskIsCompleted(User operator, User sender, Task task) {
        Message message = new Message();
        message.setTitle("Task completed");
        message.setContent("Task " + task.getTaskName() + " has been completed.");
        message.setSender(sender);
        message.setReceiver(operator);
        messageRepository.save(message);
    }

    public void notifyOperatorThatTaskIsArchived(User operator, User sender, Task task) {
        Message message = new Message();
        message.setTitle("Task archived");
        message.setContent("Task " + task.getTaskName() + " has been archived.");
        message.setSender(sender);
        message.setReceiver(operator);
        messageRepository.save(message);
    }

    public void notifyDesignerThatTaskIsArchived(User designer, User sender, Task task) {
        Message message = new Message();
        message.setTitle("Task archived");
        message.setContent("Task " + task.getTaskName() + " has been archived.");
        message.setSender(sender);
        message.setReceiver(designer);
        messageRepository.save(message);
    }

    public void notifyDesignerThatBreakTimeRequestAccepted(User designer, User sender) {
        Message message = new Message();
        message.setTitle("Break time issue request accepted");
        message.setContent("Your request has been accepted.");
        message.setSender(sender);
        message.setReceiver(designer);
        messageRepository.save(message);
    }

    public void notifyDesignerThatBreakTimeRequestDeclined(User designer, User sender) {
        Message message = new Message();
        message.setTitle("Break time issue request has been declined.");
        message.setContent("Your request has been declined.");
        message.setSender(sender);
        message.setReceiver(designer);
        messageRepository.save(message);
    }
}
