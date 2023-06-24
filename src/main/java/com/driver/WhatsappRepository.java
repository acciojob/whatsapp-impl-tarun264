package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String,User> userMap= new HashMap<>();
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 1;
        this.messageId = 1;
    }


    public String createUser(String name, String mobile) throws Exception {
        //key is the mobile number
        if(userMap.containsKey(mobile) && !userMap.isEmpty()){
            throw new Exception("User already exists");
        }
        else {
            userMobile.add(mobile);
            return "SUCCESS";
        }
    }

    public Group createGroup(List<User> users) {

        Group group= new Group();
// setting the name of group when there are only two users
        if(users.size()==2){
            group.setName(users.get(1).getName());
            group.setNumberOfParticipants(2);
        }
        else{
            String name="Group"+ " "+ customGroupCount;
            group.setName(name);
            group.setNumberOfParticipants(users.size());
            customGroupCount++;
        }
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }


    public int createMessage(String content) {
       Message message = new Message(messageId,content);
       messageId++;
       return message.getId();

    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        senderMap.put(message,sender);
        //sender will be the use so will require usersList
        List<User> usersList = new ArrayList<>(groupUserMap.get(group));
        for (User x:usersList) {
            if (sender.getMobile().equals(x.getMobile())) {
                if (groupMessageMap.containsKey(group)) {
                    groupMessageMap.get(group).add(message);
                    return groupMessageMap.get(group).size();
                } else {
                    List<Message> messages = new ArrayList<>();
                    messages.add(message);
                    groupMessageMap.put(group, messages);
                    return messages.size();
                }
            }
        }
            throw new Exception("You are not allowed to send message");
        }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }
        if(!approver.getMobile().equals(user.getMobile())){
            throw new Exception("Approver does not have rights");
        }
        List<User> usersList= new ArrayList<>(groupUserMap.get(group));
        for(User i: usersList){
            if(i.getMobile().equals(user.getMobile())){
              adminMap.put(group,user);
              return "SUCCESS";
            }
        }
        throw new Exception("User is not a participant");
    }


    public int removeUser(User user) {
        return 0;
    }

    public String findMessage(Date start, Date end, int k) {
        return "";
    }
}
