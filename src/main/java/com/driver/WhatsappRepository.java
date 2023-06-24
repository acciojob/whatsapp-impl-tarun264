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
        this.customGroupCount = 0;
        this.messageId = 0;
    }


    public String createUser(String name, String mobile) throws Exception {
        //key is the mobile number
        if(!userMap.containsKey(mobile) && !userMobile.contains(mobile)){
            User user = new User();
            user.setName(name);
            user.setMobile(mobile);
            userMap.put(mobile,user);
            userMobile.add(mobile);
            return "SUCCESS";
        }
        else{
            throw new Exception("User already exists");
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
           customGroupCount++;
            String name="Group "+ customGroupCount;
            group.setName(name);
            group.setNumberOfParticipants(users.size());
            customGroupCount++;
        }
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }


    public int createMessage(String content) {
       int id= messageId;
       id++;
        return id;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(groupUserMap.containsKey(group)){
            List<User> users = groupUserMap.get(group);

            if(users.contains(sender)){
                List<Message>messages;
                if(groupMessageMap.containsKey(group)){
                    messages= groupMessageMap.get(group);
                }
                else{
                    messages=new ArrayList<>();
                }
                messages.add(message);
                groupMessageMap.put(group,messages);
                return groupMessageMap.get(group).size();
            }
            else{
                throw new Exception("You are not allowed to send message");
            }

        }
        else{
            throw new Exception("Group does not exist");
        }
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
