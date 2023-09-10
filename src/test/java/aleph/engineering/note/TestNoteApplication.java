package aleph.engineering.note;

import org.springframework.boot.SpringApplication;

class TestNoteApplication {

	
	public static void main(String[] args) {
		SpringApplication.from(NoteApplication::main)
		.run(args);
	}

}
