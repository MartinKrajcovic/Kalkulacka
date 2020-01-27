package kalkulacka;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Test {
	public static void main(String[] args) {
		
		//vytiahnutie suborov, ktore su len zapisovatelne
		try (DirectoryStream<Path> dStream = Files.newDirectoryStream(Paths.get("C://devel"), 
		/* druhy parameter prepisuje metody accept triedy DirectoryStream.Filter<Path> pomocou lambda vyrazu */
				(Path p) -> {return Files.isWritable(p) ? true : false;}); ) {
			
			for (Path entry : dStream) {
				BasicFileAttributes attributes = Files.readAttributes(entry, BasicFileAttributes.class);
				System.out.print(attributes.isDirectory() ? "<DIR> " : "      ");
				System.out.println(entry.getFileName());
			}
			//zobrazenie celeho suboroveho stromu
			System.out.println("\n");
			Files.walkFileTree(Paths.get("C://devel//Java 1"), new C());
			
		} catch(InvalidPathException e) {
			System.err.println("Unknown destination folder..");
		} catch (IOException ee) {
			System.err.println("I/O problem..");
		}	
	}
}
/**
 * Tato trieda prepisuje metodu visitFile triedy SimpleFileVisitor,
 * kde definujeme, ako sa ma spravat prehliadanie stromu priecinka.
 * Metoda vracia konstantu, ktora zabezpecuje pokracovanie az do
 * konca.
 * @author mkrajcovic
 *
 */
class C extends SimpleFileVisitor<Path> {
	@Override
	public FileVisitResult visitFile(Path p, BasicFileAttributes b) {
	    System.out.println(p);
		return FileVisitResult.CONTINUE;
	}
}