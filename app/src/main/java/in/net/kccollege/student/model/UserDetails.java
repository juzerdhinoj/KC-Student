package in.net.kccollege.student.model;

/**
 * Created by Sahil on 11-07-2017.
 */

public class UserDetails {

	private String name;
	private String unique;
	private String email;
	private String details;
	private String created_at;

	public UserDetails(String name, String unique, String email, String details, String created_at) {
		this.name = name;
		this.unique = unique;
		this.email = email;
		this.details = details;
		this.created_at = created_at;
	}

	public String getName() {
		return name;
	}

	public String getUnique() {
		return unique;
	}

	public String getEmail() {
		return email;
	}

	public String getDetails() {
		return details;
	}

	public String getCreated_at() {
		return created_at;
	}
}
