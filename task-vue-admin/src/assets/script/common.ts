import {Message} from '@arco-design/web-vue';
import { Notification } from '@arco-design/web-vue';

export class Constants{
    static readonly TOKEN_KEY: string = "go-task-token"
}

export class Msg {
    static async success(message: string) {
        Message.success({
            content: message
        })
    }
    static async error(message: string) {
        Message.error({
            content: message
        })
    }
    static async waring(message: string) {
        Message.warning({
            content: message
        })
    }
    static async info(message: string) {
        Message.info({
            content: message
        })
    }
    static loading(message: string) {
        return  Message.loading({
            content: message
        })
    }
}

export class Not {
    static async info(message: string) {
        Notification.info({
            content: message
        })
    }
    static async success(message: string) {
        Notification.success({
            content: message
        })
    }
    static async error(message: string) {
        Notification.error({
            content: message
        })
    }
}