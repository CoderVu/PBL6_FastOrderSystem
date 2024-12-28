package com.example.BE_PBL6_FastOrderSystem.controller.Chat;
import com.example.BE_PBL6_FastOrderSystem.entity.Chat;
import com.example.BE_PBL6_FastOrderSystem.entity.User;
import com.example.BE_PBL6_FastOrderSystem.request.ChatImageRequest;
import com.example.BE_PBL6_FastOrderSystem.request.ChatRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIResponseChat;
import com.example.BE_PBL6_FastOrderSystem.response.ChatResponse;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IChatService;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private IChatService chatService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IStoreService storeService;

    @GetMapping
    public ResponseEntity<APIResponseChat<List<Chat>>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        APIResponseChat<List<Chat>> response = new APIResponseChat<>(chats, 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<APIResponseChat<List<Chat>>> getChatsBySender(@PathVariable Long senderId) {
        List<Chat> chats = chatService.getChatsBySender(senderId);
        APIResponseChat<List<Chat>> response = new APIResponseChat<>(chats, 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<APIResponseChat<List<ChatResponse>>> getChatHistory(@PathVariable Long receiverId) {
        List<ChatResponse> chats = chatService.getChatHistory(receiverId);
        APIResponseChat<List<ChatResponse>> response = new APIResponseChat<>(chats, 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<APIResponseChat<Chat>> createChat(@RequestBody ChatRequest chat) {
        Chat createdChat = chatService.saveChat(chat);
        APIResponseChat<Chat> response = new APIResponseChat<>(createdChat, 0, "Chat created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/users/received-from/{userId}")
    public ResponseEntity<APIResponseChat<Set<User>>> getUsersReceivedFrom(@PathVariable Long userId) {
        Set<User> users = chatService.getUsersReceivedFrom(userId);
        APIResponseChat<Set<User>> response = new APIResponseChat<>(users, 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/sent-to/{userId}")
    public ResponseEntity<APIResponseChat<Set<User>>> getUsersSentTo(@PathVariable Long userId) {
        Set<User> users = chatService.getUsersSentTo(userId);
        APIResponseChat<Set<User>> response = new APIResponseChat<>(users, 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/interacted-with/{userId}")
    public ResponseEntity<APIResponseChat<Set<User>>> getUsersInteractedWith(@PathVariable Long userId) {
        Set<User> users = chatService.getUsersInteractedWith(userId);
        APIResponseChat<Set<User>> response = new APIResponseChat<>(users, 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    Lấy những tin nhắn chưa đọc từ ngươ nhận là người hiện tại
    @GetMapping("/users/unread")
    public ResponseEntity<APIResponseChat<List<User>>> markAsRead() {
        List<User> users = chatService.getUserMessagesUnRead();
        APIResponseChat<List<User>> response = new APIResponseChat<>(users, 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/read")
    public ResponseEntity<APIResponseChat<String>> updateRead(@RequestParam Long userId){
        System.out.println("updateRead");
        System.out.println(userId);
        APIResponseChat<String> read = chatService.updateReadUser(userId);
         return ResponseEntity.ok(read);
    }

    @GetMapping("/mark-messages-as-read/{userId}")
    public ResponseEntity<APIResponseChat<String>> markMessagesAsRead(@PathVariable Long userId) {
        chatService.markMessagesAsReadForUser(userId);
        APIResponseChat<String> response = new APIResponseChat<>("", 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/saveImage")
    public ResponseEntity<APIResponseChat<String>> saveChat(
            @RequestParam("sender") Long sender,
            @RequestParam("receiver") Long receiver,
            @RequestParam(value = "image", required = false) MultipartFile image, // Nhận file ảnh từ form
            @RequestParam(value = "isRead", required = false) Boolean isRead) {

        // Tạo ChatRequest từ dữ liệu nhận được
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setSender(sender);
        chatRequest.setReceiver(receiver);
        chatRequest.setMessage(null);
        chatRequest.setIsRead(isRead != null ? isRead : false); // Mặc định là chưa đọc
        chatRequest.setImage(image);

        // Lưu thông tin chat bao gồm ảnh (nếu có)
        chatService.saveChat(chatRequest);
        APIResponseChat<String> response = new APIResponseChat<>("", 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/saveImages")
    public ResponseEntity<APIResponseChat<String>> saveChatV2(
            @RequestBody ChatImageRequest request) {
        // Lưu thông tin chat bao gồm ảnh (nếu có)

        chatService.saveChatV2(request);
        APIResponseChat<String> response = new APIResponseChat<>("", 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users-chat")
    public APIResponseChat<List<UserResponse>> getAllUsersChat() {

        List<UserResponse> users = chatService.getAllUsersByChatInteraction();
        return new APIResponseChat<>(users, 0, "Data retrieved successfully");
    }

    @GetMapping("/search/{phoneNumber}")
    public APIResponseChat<List<UserResponse>> getSearchByPhoneNumber(@PathVariable String phoneNumber) {
        List<UserResponse> users = userService.getSearchByPhoneNumber(phoneNumber);
        return new APIResponseChat<>(users, 0, "Data retrieved successfully");
    }

    @GetMapping("/searchOwner/{idStore}")
    public APIResponseChat<UserResponse> getOwnerForStore(@PathVariable  Long idStore){
        return storeService.getOwnerForStore(idStore);
    }

    @GetMapping("/all/owner/store")
    public APIResponseChat<List<Long>> getAllOwnerStore(){
        return  chatService.allIdOwnerOfStores();
    }

    @GetMapping("/findStore/{id}")
    public  APIResponseChat<Long> findStoreByOwner(@PathVariable Long id){
        System.out.println("findStoreByOwner");
        return chatService.findStoreByOwner(id);
    }
    @PostMapping("/saveMessage")
    public ResponseEntity<APIResponseChat<String>> saveMessage(
            @RequestParam("sender") Long sender,
            @RequestParam("receiver") Long receiver,
            @RequestParam(value = "mess", required = false) String mess, // Nhận file ảnh từ form
            @RequestParam(value = "isRead", required = false) Boolean isRead) {

        // Tạo ChatRequest từ dữ liệu nhận được
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setSender(sender);
        chatRequest.setReceiver(receiver);
        chatRequest.setMessage(null);
        chatRequest.setIsRead(isRead != null ? isRead : false); // Mặc định là chưa đọc
        chatRequest.setMessage(mess);

        // Lưu thông tin chat bao gồm ảnh (nếu có)
        chatService.saveChat(chatRequest);
        APIResponseChat<String> response = new APIResponseChat<>("", 0, "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
