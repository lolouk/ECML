package com.login;

public class User {
	private long id;
	private String login;
	private String password;
	private String question ;
	private String answer ;
    
	public User ( String login, String password) {
		    super();
		   // this.id = id;
		    this.login = login;
		    this.password = password;
		  }
   public User ( String login , String password ,String question ,String answer  ){
	   super();
	   this.login = login;
	   this.password = password;
	   this.question=question;
	   this.answer=answer;
   }
		  public long getId() {
		    return id;
		  }

		  public void setId(long id) {
		    this.id = id;
		  }

		  public String getLogin() {
		    return this.login;
		  }

		  public void setIntitule(String login) {
		    this.login = login;
		  }

		  public String getPassword() {
		    return this.password;
		  }

		  public void setPassword(String password) {
		    this.password = password;
		  }
		  
		  public String getQuestion() {
			    return this.question;
			  }
		  public String getAnswer() {
			    return this.answer;
			  }
		  

}
