package fi.minedu.oiva.backend.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.core.util.FileUtils.PathBuilder.pathify;
import static java.io.File.separator;

/**
 * Kokoelma utiliteettimetodeita tiedostojen käsittelyyn
 */
public final class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {}

    public static Path makeDir(final String path) throws IOException {
        return Files.createDirectories(Paths.get(path));
    }

    public static void deleteDirs(final String rootPath, final String path) throws IOException {
        final Path fullPath = Paths.get(pathify(rootPath, path));
        final DirectoryStream<Path> ds = Files.newDirectoryStream(fullPath);
        if (!ds.iterator().hasNext()) {
            Files.delete(fullPath);
            final int index = path.lastIndexOf('/');
            if (index > 0) {
                String nextPath = path.substring(0, index);
                deleteDirs(rootPath, nextPath);
            }
        }
    }

    /**
     * Formatoi tiedostokoot ihmisten luettavaan muotoon
     */
    public static String formatFileSize(final long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String normalizeFileName(final String fileName) {
        return Normalizer
            .normalize(fileName, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "").replace(" ", "_");
    }

    public static Optional<File> getFileOptFromPath(final String path) {
        try {
            return Optional.of(Paths.get(path).toFile());
        } catch (InvalidPathException | UnsupportedOperationException e) {
            logger.error("Could not find file from path!", e);
            return Optional.empty();
        }
    }

    /**
     * Tiedostolle generoidaan uusi polku päivämäärällä ja
     * (pseudo)satunnaisella UUID:lla.
     * Muoto: /[vuosi]/[kuukausi]/[päivä]/[UUID]
     *
     * @return Tiedoston polku (ei sisällä tiedostonimeä)
     */
    public static String generateUniqueFilePath() {
        final LocalDateTime now = LocalDateTime.now();
        final String uuid = UUID.randomUUID().toString();
        return pathify(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), uuid);
    }

    /**
     * Utiliteetti "system-safe":ille tiedostopoluille
     */
    public static class PathBuilder {
        private StringBuilder sb;

        public PathBuilder(Object... pathComponents) {
            this.sb = new StringBuilder();
            int i = 0;
            for (Object pathComponent : pathComponents) {
                if (i != 0) {
                    sb.append(separator);
                }
                sb.append(pathComponent);
                i++;
            }
        }

        /**
         * Oikopolkumetodi static importtia varten
         *
         * @param pathComponents
         * @return String
         */
        public static String pathify(final Object... pathComponents) {
            return new PathBuilder(pathComponents).build();
        }

        public static PathBuilder createPath(final Object... pathComponents) {
            return new PathBuilder(pathComponents);
        }

        public PathBuilder withTailSeparator() {
            sb.append(separator);
            return this;
        }

        public PathBuilder withHeadSeparator() {
            StringBuilder newSb = new StringBuilder();
            sb = newSb.append(separator).append(sb);
            return this;
        }

        public String build() {
            return sb.toString();
        }
    }
}
