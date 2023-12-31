/**
 * 格式化rfc3339格式的时间为 yyyy-MM-dd HH:mm:ss类型
 * @param dateTime
 */
export const formatDateTime = (dateTime: string) => {
    if (!dateTime) {
        return "";
    }
    const date = new Date(dateTime);
    const year = String(date.getFullYear());
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

/**
 *
 * @param number
 */
export const formatSize = (number: number): number => {
    const num = (number / 1024 / 1024 / 1024);
    return Number(num.toFixed(2))
}