package com.abdulrahim.studentregistration.dto;

import lombok.Data;

@Data
public class DepartmentResponse {

   private Long id;

   private String name;
}

/*
* The DB has more knowledge of the departments,
* if a user is allowed to assign an id for a department,
* maybe that id has already been assigned to another department.
* so the best, leave that one to the DB, it knows the structure better than all.
* The client does not know how many departments are in the DB and their ids.
* It makes no sense for the client to assign an id.

Both. How can the DTO return the description of a department if a client requests for it?
* let say our DTO has name and description, can it return a description without it being in the entity?
* To modify the DTO it depends if the company want to return it to clients.
* They have the choice to add it to DTO or not. But in reality,
* it indeed makes sense when a client request for a department and it comes with a description instead of just the name.
* So I choose both, but it still depends.
*
* */
