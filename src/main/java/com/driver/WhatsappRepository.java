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
        if(userMobile.contains(mobile)){
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


    public int removeUser(User user) throws Exception {
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

//        private HashMap<Group, List<User>> groupUserMap;
        int sum=0;

        for (Group x:groupUserMap.keySet() ) {
            List <User> users= groupUserMap.get(x);
            for(User i: users){
                //If user is found in a group
                    if(i.getMobile().equals(user.getMobile())) {
                        //If user is found in a group and it is the admin
                        if (adminMap.get(x).equals(i)) {
                            throw new Exception("Cannot remove the admin");
                        } else {
                            List<Message> messages = new ArrayList<>(groupMessageMap.get(x));
                            for (Message m : messages) {
                                // removing from sender map
                                if (senderMap.get(m).getMobile().equals(user.getMobile())) {
                                    // removing from message list
                                    messages.remove(m);
                                    senderMap.remove(m);
                                }
                                groupMessageMap.get(x).remove(m);
                            }
                        }
                        groupUserMap.remove(i);
                        x.setNumberOfParticipants(users.size());

                    }
                    else {
                        throw new Exception("User not found");
                    }
            //    the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)
                    sum+=x.getNumberOfParticipants()+ groupMessageMap.get(x).size()+senderMap.size();
            }

        }
        return sum;
    }

    public String findMessage(Date start, Date end, int k) throws Exception {
        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception
        List<Message>messages =new ArrayList<>();
        for (Message i:senderMap.keySet()) {
            if(i.getTimestamp().after(start) && i.getTimestamp().before(end)){
                messages.add(i);
            }
        }
        if(messages.size()<k){
            throw new Exception("K is greater than the number of messages");
        }
            return messages.get(messages.size()-1).getContent();
    }

    }

