declare module "*.vue" {
    import { ComponentOptions } from 'vue'
    const componentOptions: ComponentOptions
    export default componentOptions
}

interface ImportMetaEnv {
    VITE_BASE_URL: string
    BASE_URL: string
}
interface ImportMeta {
    env: ImportMetaEnv
}