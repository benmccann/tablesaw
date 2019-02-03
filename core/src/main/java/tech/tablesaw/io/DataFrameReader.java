/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.html.HtmlTableReader;
import tech.tablesaw.io.jdbc.SqlResultSetReader;
import tech.tablesaw.io.json.JsonReader;

public class DataFrameReader {

    public Table csv(String file) throws IOException {
        return csv(CsvReadOptions.builder(file));
    }

    public Table csv(String contents, String tableName) {
        try {
            return csv(new StringReader(contents), tableName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Table csv(File file) throws IOException {
        return csv(CsvReadOptions.builder(file));
    }

    public Table csv(InputStream stream, String tableName) throws IOException {
        return csv(CsvReadOptions.builder(stream, tableName));
    }

    public Table csv(Reader reader, String tableName) throws IOException {
        return csv(CsvReadOptions.builder(reader, tableName));
    }

    public Table csv(CsvReadOptions.Builder options) throws IOException {
        return csv(options.build());
    }

    public Table csv(CsvReadOptions options) throws IOException {
        return new CsvReader().read(options);
    }

    public Table json(String url) throws MalformedURLException, IOException {
        try (Scanner scanner = new Scanner(new URL(url).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return json(scanner.hasNext() ? scanner.next() : "", url);
	}
    }

    public Table json(String contents, String tableName) throws IOException {
	JsonReader.Csv csv = new JsonReader().jsonToCsv(contents);
        return csv(CsvReadOptions
        	.builder(new StringReader(csv.getContents()), tableName)
        	.header(csv.hasHeader()));
    }

    public Table db(ResultSet resultSet, String tableName) throws SQLException {
        return SqlResultSetReader.read(resultSet, tableName);
    }

    public Table html(String url) throws IOException {
        return new HtmlTableReader().read(url);
    }
}
