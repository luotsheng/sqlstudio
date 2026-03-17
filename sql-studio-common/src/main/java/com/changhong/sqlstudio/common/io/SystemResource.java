package com.changhong.sqlstudio.common.io;

/* -------------------------------------------------------------------------------- *\
|*                                                                                  *|
|*    Copyright (C) 2019-2024 RedGogh All rights reserved.                          *|
|*                                                                                  *|
|*    Licensed under the Apache License, Version 2.0 (the "License");               *|
|*    you may not use this file except in compliance with the License.              *|
|*    You may obtain a copy of the License at                                       *|
|*                                                                                  *|
|*        http://www.apache.org/licenses/LICENSE-2.0                                *|
|*                                                                                  *|
|*    Unless required by applicable law or agreed to in writing, software           *|
|*    distributed under the License is distributed on an "AS IS" BASIS,             *|
|*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.      *|
|*    See the License for the specific language governing permissions and           *|
|*    limitations under the License.                                                *|
|*                                                                                  *|
\* -------------------------------------------------------------------------------- */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changhong.sqlstudio.common.collection.Lists;
import com.changhong.sqlstudio.common.exception.IOReadException;
import com.changhong.sqlstudio.common.exception.IllegalOperatorException;
import com.changhong.sqlstudio.common.string.StringUtils;
import com.changhong.sqlstudio.common.system.SystemUtils;
import com.changhong.sqlstudio.common.utils.Assert;
import com.changhong.sqlstudio.common.utils.Captor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * 扩展了 `java.io.File` 的自定义文件类，提供了增强的文件操作功能。
 * <p>
 * 该类在基本文件操作功能的基础上，增加了对文件路径的快速访问、文件扩展名的处理、文件强制删除等功能。
 * 它支持在 Windows 操作系统上对路径进行快速替换，提供了便捷的方法来获取和操作文件的扩展名。
 * <p>
 * 此外，本类还提供了对文件内容的随机读写操作的支持，包括读取不同类型的数据（如 int、long、float、double）。
 * 强制删除文件或目录的方法会递归删除目录下的所有内容，并尝试删除指定的文件或目录。
 * <p>
 * 本类支持的主要功能包括：
 * <ul>
 *     <li>路径快速访问</li>
 *     <li>文件和目录的强制删除</li>
 *     <li>文件扩展名的处理</li>
 *     <li>文件内容的随机读写访问</li>
 * </ul>
 * <p>
 * 需要注意的是，文件的随机读写访问功能要求在操作之前调用 `open()` 方法打开文件描述符。
 *
 * @see File
 * @since 1.0
 * @author Luo Tiansheng
 * @noinspection UnusedReturnValue
 */
public class SystemResource extends File {

    /**
     * 空文件数组常量，避免不必要的对象创建。
     */
    private static final SystemResource[] EMPTY_PHYSICAL_FILE_ARRAY = new SystemResource[0];

    /**
     * 桌面路径变量标识符，用于指示桌面目录的路径替换。
     */
    private static final String PATHNAME_DESKTOP_VARIABLE = "Desktop://";

    /**
     * 用户主目录路径变量标识符，用于指示用户主目录的路径替换。
     */
    private static final String PATHNAME_USER_HOME_VARIABLE = "Home://";

    /**
     * `FileInputStreamResource` 用于处理 `FileInputStream` 资源的函数式接口。
     *
     * <p>该接口定义了一个 `apply` 方法，允许对 `FileInputStream` 进行自定义处理。
     * 适用于资源管理，如文件读取、解析等操作。
     */
    public interface FileInputStreamResource {
        /**
         * 处理 `FileInputStream` 资源。
         *
         * @param inputStream 文件输入流
         * @throws Exception 处理过程中可能抛出的异常
         */
        void apply(FileInputStream inputStream) throws Exception;
    }

    /**
     * `FileOutputStreamResource` 用于处理 `FileOutputStream` 资源的函数式接口。
     *
     * <p>该接口定义了一个 `apply` 方法，允许对 `FileOutputStream` 进行自定义处理。
     * 适用于资源管理，如文件写入、加密等操作。
     */
    public interface FileOutputStreamResource {
        /**
         * 处理 `FileOutputStream` 资源。
         *
         * @param outputStream 文件输出流
         * @throws Throwable 处理过程中可能抛出的异常
         */
        void apply(FileOutputStream outputStream) throws Throwable;
    }

    /**
     * #brief: 快速访问指定路径
     *
     * <p>该函数用于在 Windows 操作系统上对指定路径进行快速替换操作。
     * 如果传入的路径以特定的桌面路径变量（{@code PATHNAME_DESKTOP_VARIABLE}）开头，
     * 将替换为实际的用户桌面路径。
     *
     * <p>注意事项：
     * - 该函数仅在 Windows 操作系统上执行替换操作，其他操作系统则直接返回原始路径。
     * - 确保 {@code PATHNAME_DESKTOP_VARIABLE} 已正确定义且匹配预期的路径格式。
     *
     * @param pathname
     *        要进行快速访问转换的路径字符串
     *
     * @return 如果是 Windows 操作系统且路径匹配桌面路径变量，返回替换后的路径；
     *         否则，返回原始路径
     */
    private static String quickAccessPath(String pathname) {
        if (SystemUtils.isWindows() || SystemUtils.isMacOS()) {
            /* Conversation Desktop */
            if (pathname.startsWith(PATHNAME_DESKTOP_VARIABLE))
                return pathname.replace(PATHNAME_DESKTOP_VARIABLE, SystemUtils.getUserHome("Desktop/"));

            /* Conversation Home */
            if (pathname.startsWith(PATHNAME_USER_HOME_VARIABLE))
                return pathname.replace(PATHNAME_USER_HOME_VARIABLE, SystemUtils.getUserHome(""));
        }

        return pathname;
    }

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param   pathname  A pathname string
     * @throws  NullPointerException
     *          If the <code>pathname</code> argument is <code>null</code>
     */
    public SystemResource(String pathname) {
        super(quickAccessPath(pathname));
    }

    /**
     * Creates a new <code>File</code> instance from a parent pathname string
     * and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> pathname string is taken to denote
     * a directory, and the <code>child</code> pathname string is taken to
     * denote either a directory or a file.  If the <code>child</code> pathname
     * string is absolute then it is converted into a relative pathname in a
     * system-dependent way.  If <code>parent</code> is the empty string then
     * the new <code>File</code> instance is created by converting
     * <code>child</code> into an abstract pathname and resolving the result
     * against a system-dependent default directory.  Otherwise each pathname
     * string is converted into an abstract pathname and the child abstract
     * pathname is resolved against the parent.
     *
     * @param   parent  The parent pathname string
     * @param   child   The child pathname string
     * @throws  NullPointerException
     *          If <code>child</code> is <code>null</code>
     */
    public SystemResource(String parent, String child) {
        super(parent, child);
    }

    /**
     * Creates a new <code>File</code> instance from a parent abstract
     * pathname and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> abstract pathname is taken to
     * denote a directory, and the <code>child</code> pathname string is taken
     * to denote either a directory or a file.  If the <code>child</code>
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If <code>parent</code> is the empty
     * abstract pathname then the new <code>File</code> instance is created by
     * converting <code>child</code> into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     *
     * @param   parent  The parent abstract pathname
     * @param   child   The child pathname string
     * @throws  NullPointerException
     *          If <code>child</code> is <code>null</code>
     */
    public SystemResource(File parent, String child) {
        super(parent, child);
    }

    /**
     * Creates a new <tt>File</tt> instance by converting the given
     * <tt>file:</tt> URI into an abstract pathname.
     *
     * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     *
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     *
     * <blockquote><tt>
     * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     *
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a <tt>file:</tt> URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param  uri
     *         An absolute, hierarchical URI with a scheme equal to
     *         <tt>"file"</tt>, a non-empty path component, and undefined
     *         authority, query, and fragment components
     *
     * @throws  NullPointerException
     *          If <tt>uri</tt> is <tt>null</tt>
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on the parameter do not hold
     *
     * @see #toURI()
     * @see URI
     * @since 1.4
     */
    public SystemResource(URI uri) {
        super(uri);
    }

    /**
     * 通过一个 {@link File} 对象来构建一个 {@link SystemResource} 对象实例，并且参数 File
     * 不能是空的。
     *
     * @param file java.io 下的 File 对象实例
     */
    public SystemResource(File file) {
        this(file.getPath());
    }

    /**
     * 将指定的 `java.io.File` 对象包装为自定义的 `File` 对象。
     * <p>
     * 该方法接收一个标准的 `java.io.File` 对象，并将其包装为当前定义的自定义 `File` 对象，以便进一步处理。
     *
     * @param file 要包装的 `java.io.File` 对象。
     * @return 一个新的 `File` 对象，包装了传入的 `java.io.File` 对象。
     */
    public static SystemResource from(File file) {
        if (file instanceof SystemResource)
            return (SystemResource) file;
        return new SystemResource(file);
    }

    /**
     * Returns the abstract pathname of this abstract pathname's parent,
     * or {@code null} if this pathname does not name a parent
     * directory.
     *
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name
     * sequence except for the last.  If the name sequence is empty then
     * the pathname does not name a parent directory.
     *
     * @return  The abstract pathname of the parent directory named by this
     *          abstract pathname, or {@code null} if this pathname
     *          does not name a parent
     *
     * @since 1.2
     */
    @Override
    public SystemResource getParentFile() {
        File parentFile = super.getParentFile();
        return parentFile != null ? from(parentFile) : null;
    }

    /**
     * Returns an array of abstract pathnames denoting the files in the
     * directory denoted by this abstract pathname.
     *
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns {@code null}.  Otherwise an array of {@code File} objects
     * is returned, one for each file or directory in the directory.  Pathnames
     * denoting the directory itself and the directory's parent directory are
     * not included in the result.  Each resulting abstract pathname is
     * constructed from this abstract pathname using the {@link #SystemResource(File,
     * String) File(File,&nbsp;String)} constructor.  Therefore if this
     * pathname is absolute then each resulting pathname is absolute; if this
     * pathname is relative then each resulting pathname will be relative to
     * the same directory.
     *
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#newDirectoryStream(Path) newDirectoryStream} method
     * to open a directory and iterate over the names of the files in the
     * directory. This may use less resources when working with very large
     * directories.
     *
     * @return  An array of abstract pathnames denoting the files and
     *          directories in the directory denoted by this abstract pathname.
     *          The array will be empty if the directory is empty.  Returns
     *          {@code null} if this abstract pathname does not denote a
     *          directory, or if an I/O error occurs.
     *
     * @since  1.2
     */
    @Override
    public SystemResource[] listFiles() {
        File[] a = super.listFiles();

        if (a == null || a.length == 0)
            return EMPTY_PHYSICAL_FILE_ARRAY;

        return Lists.map(a, SystemResource::from).toArray(new SystemResource[0]);
    }

    /**
     * @return 返回一个干净的文件名称，指不带扩展后缀的
     *         文件名
     */
    public String getCleanName() {
        String name = getName();
        return StringUtils.strcut(getName(), 0, name.lastIndexOf("."));
    }

    /**
     * 比较两个文件的扩展类型是否一致。如果扩展名一致的话则返回 `true`。文件
     * 的扩展名需要带 '.'，如：.pdf
     *
     * @param extension 文件扩展名
     * @return 当前 File 文件和 {@code extension} 一致返回 `true`
     */
    public boolean typeEquals(String extension) {
        return StringUtils.streq(extension, getExtension());
    }

    /**
     * #brief: 检查文件扩展名是否匹配
     *
     * <p>该函数用于检查当前对象的文件扩展名是否匹配指定的扩展名列表。
     *
     * @param extensions
     *        需要匹配的文件扩展名列表，可变参数形式
     *
     * @return 如果当前对象的扩展名与任一指定的扩展名匹配，返回 {@code true}；
     *         否则返回 {@code false}
     */
    public boolean typeMatch(String... extensions) {
        return StringUtils.strcheckin(getExtension(), extensions);
    }

    /**
     * 获取文件扩展名（不包含 `.`）。
     *
     * <p>该方法查找文件名中的 `.` 符号，并返回 `.` 之后的部分作为扩展名。
     * 如果文件名中没有 `.`，则返回空字符串。
     *
     * @return 文件扩展名（无 `.` 前缀），如果没有扩展名则返回 `""`
     */
    public String getExtension() {
        String name = getName();
        int index = name.indexOf(".");
        if (index == -1)
            return "";
        return StringUtils.strcut(name, index, 0);
    }

    private boolean forceDeleteDirectory(SystemResource dir) {
        List<SystemResource> children = Lists.map(dir.listFiles(), SystemResource::new);
        for (SystemResource child : children) {
            if (child.isFile()) {
                child.forceDeleteFile();
            } else {
                child.forceDeleteDirectory();
            }
        }
        return dir.forceDeleteFile();
    }

    private boolean forceDeleteDirectory() {
        return forceDeleteDirectory(this);
    }

    private boolean forceDeleteFile() {
        boolean retval;
        if (!(retval = delete())) {
            System.gc();
            retval = delete();
        }
        return retval;
    }

    /**
     * 强制删除一个文件或目录，文件或目录必须存在，否则将什么都不做。如果是目录
     * 会先递归删除目录下的所有文件，然后再删除目录本身。
     *
     * @return 是否删除成功
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean forceDelete() {
        if (exists())
            return isFile() ? forceDeleteFile() : forceDeleteDirectory();
        return false;
    }

    /**
     * 检查文件是否存在，如果不存在就创建一个，但前提是 {@code enableAutoCreate}
     * 参数必须是 {@code true}。
     */
    private void checkFile(boolean autoCreate) {
        if (autoCreate && !exists())
            Captor.call(this::createNewFile);
        Assert.isTrue(exists(), "%s （系统找不到指定的文件，文件不存在）", getPath());
    }

    /**
     * 打开一个文件输入流，通过参数 {@code autoCreate} 检查当文件不存在时是否
     * 自动创建一个新的文件。<p>
     *
     * 如果 {@code autoCreate} 为 {@code false} 时，当文件不存在就会抛出一个
     * 运行时断言异常。
     *
     * @param autoCreate 文件不存在时是否创建
     * @return 打开描述符后的文件输入流对象
     */
    private FileInputStream openInputStream(boolean autoCreate) {
        checkFile(autoCreate);
        return Captor.call(() -> new FileInputStream(this));
    }

    /**
     * 打开一个文件输出流，通过参数 {@code autoCreate} 检查当文件不存在时是否
     * 自动创建一个新的文件。<p>
     *
     * 如果 {@code autoCreate} 为 {@code false} 时，当文件不存在就会抛出一个
     * 运行时断言异常。
     *
     * @param autoCreate 文件不存在时是否创建
     * @return 打开描述符后的文件输出流对象
     */
    private FileOutputStream openOutputStream(boolean autoCreate) {
        checkFile(autoCreate);
        return Captor.call(() -> new FileOutputStream(this));
    }

    /**
     * 打开文件输入流，如果文件不存在则会自动创建一个新的空文件。避免当文件
     * 不存在时系统抛出 FileNotFound 异常。
     *
     * @return 打开描述符后的文件输入流对象
     */
    public FileInputStream openInputStream() {
        return openInputStream(true);
    }

    /**
     * 打开文件输出流，如果文件不存在则会自动创建一个新的空文件。避免当文件
     * 不存在时系统抛出 FileNotFound 异常。
     *
     * @return 打开描述符后的文件输入流对象
     */
    public FileOutputStream openOutputStream() {
        return openOutputStream(true);
    }

    /**
     * 打开文件输入流，如果当文件不存在时则会抛出一个 FileNotFound 异常，该函数
     * 不会自动创建文件。
     *
     * @return 打开描述符后的文件输入流对象
     */
    public FileInputStream dopenInputStream() {
        return openInputStream(false);
    }

    /**
     * 打开文件输出流，如果当文件不存在时则会抛出一个 FileNotFound 异常，该函数
     * 不会自动创建文件。
     *
     * @return 打开描述符后的文件输出流对象
     */
    public FileOutputStream dopenOutputStream() {
        return openOutputStream(false);
    }

    /**
     * 安全地执行 `FileInputStream` 操作，确保资源正确关闭。
     *
     * <p>该方法使用 `try-with-resources` 结构，确保 `FileInputStream` 资源在操作完成后自动关闭。
     * 若操作过程中发生异常，则会抛出 `IOReadException`。
     *
     * @param resource 需要执行的 `FileInputStreamResource` 操作
     * @throws IOReadException 如果文件读取过程中发生错误
     */
    public void openInputStream(FileInputStreamResource resource) {
        try (FileInputStream stream = openInputStream()) {
            resource.apply(stream);
        } catch (Throwable e) {
            throw new IOReadException(e);
        }
    }

    /**
     * 安全地执行 `FileOutputStream` 操作，确保资源正确关闭。
     *
     * <p>该方法使用 `try-with-resources` 结构，确保 `FileOutputStream` 资源在操作完成后自动关闭。
     * 若操作过程中发生异常，则会抛出 `IOReadException`。
     *
     * @param resource 需要执行的 `FileOutputStreamResource` 操作
     * @throws IOReadException 如果文件写入过程中发生错误
     */
    public void openOutputStream(FileOutputStreamResource resource) {
        try (FileOutputStream stream = openOutputStream()) {
            resource.apply(stream);
        } catch (Throwable e) {
            throw new IOReadException(e);
        }
    }

    /**
     * #brief: 读取并返回文件的所有字节内容
     *
     * <p>该方法打开文件，读取文件的所有内容并将其存储到字节数组中，最后关闭文件。
     * 返回包含文件所有字节数据的数组。
     *
     * <p>方法会先根据文件长度初始化一个字节数组，然后将文件内容读取到该数组中。
     * 调用完毕后，会自动关闭文件。
     *
     * @return 包含文件内容的字节数组
     */
    public byte[] readAllBytes() {
        byte[] b = new byte[(int) length()];
        openRandomAccess(fd -> fd.read(b));
        return b;
    }

    //////////////////////////////////////////////////////////////////////////////
    /// RandomAccessFile
    //////////////////////////////////////////////////////////////////////////////

    /**
     * 定义一个接口，用于在随机访问文件中执行特定的操作。
     * <p>
     * 该接口接受一个 {@link RandomAccessFile} 对象，并返回操作的结果。
     * </p>
     *
     * @param <T> 操作结果的类型
     */
    public interface RandomAccessResource<T> {
        /**
         * 在 {@link RandomAccessFile} 上执行操作。
         *
         * @param fd {@link RandomAccessFile} 文件描述符
         * @return 操作的结果
         * @throws Throwable 在操作过程中发生异常时抛出
         */
        T apply(RandomAccessFile fd) throws Throwable;
    }

    /**
     * 打开一个随机访问文件并执行指定的操作。
     * <p>
     * 在执行完操作后，文件会自动关闭。若操作过程中发生异常，
     * 将会抛出一个 {@link IllegalOperatorException} 异常。
     * </p>
     *
     * @param <T> 操作结果的类型
     * @param resource 要执行的操作
     * @return 操作结果
     */
    public <T> T openRandomAccess(RandomAccessResource<T> resource) {
        try (RandomAccessFile fd = new RandomAccessFile(this, "rwd")) {
            return resource.apply(fd);
        } catch (Throwable e) {
            throw new IllegalOperatorException(e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    /// CVT
    //////////////////////////////////////////////////////////////////////////////

    /**
     * 加载配置文件的属性。
     * <p>
     * 该方法从指定的字节读取器中加载属性文件，并返回包含所有属性键值对的 `Properties` 对象。
     * 加载过程中，使用 `Capturer.call()` 方法来捕获可能的异常。
     *
     * @return 加载的 `Properties` 对象，包含从配置文件中读取的所有键值对。
     */
    public Properties toProperties() {
        Properties properties = new Properties();
        try(FileInputStream FileInputStream = dopenInputStream()) {
            properties.load(FileInputStream);
        } catch (Exception e) {
            throw new IOReadException(e);
        }
        return properties;
    }

    /**
     * 将文件内容解析为 {@link JSONObject}。
     *
     * <p>该方法假定文件内容是合法的 JSON 格式，并尝试将其解析为 {@link JSONObject} 对象。
     * 如果解析失败，可能会抛出 JSON 解析异常。
     *
     * @return 解析后的 {@link JSONObject} 对象
     */
    public JSONObject toJSONObject() {
        return (JSONObject) JSONObject.parse(readAllBytes());
    }

    /**
     * 将文件内容解析为 {@link JSONArray}。
     *
     * <p>该方法假定文件内容是合法的 JSON 数组格式，并尝试将其解析为 {@link JSONArray} 对象。
     * 如果解析失败，可能会抛出 JSON 解析异常。
     *
     * @return 解析后的 {@link JSONArray} 对象
     */
    public JSONArray toJSONArray() {
        return (JSONArray) JSONArray.parse(readAllBytes());
    }

}
