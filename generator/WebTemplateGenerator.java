package Generator.generator;

import Generator.config.AuthConfig;
import Generator.config.GeneratorConfig;
import Generator.config.JwtConfig;
import Generator.model.TableInfo;
import Generator.model.ColumnInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebTemplateGenerator {
    private final GeneratorConfig generatorConfig;
    private final AuthConfig authConfig;
    private final JwtConfig jwtConfig;

    public WebTemplateGenerator(GeneratorConfig generatorConfig, AuthConfig authConfig, JwtConfig jwtConfig) {
        this.generatorConfig = generatorConfig;
        this.authConfig = authConfig;
        this.jwtConfig = jwtConfig;
    }

    public void generateAll(List<TableInfo> tables) throws IOException {
        generateCss();
        generateAppJs();
        generateApiJs();
        generateAuthJs();
        generateToastJs();
        generateTableJs();
        generateIndexHtml(tables);
        generateLoginHtml();
        generateRegisterHtml();

        for (TableInfo table : tables) {
            generateTableHtml(table);
        }

        System.out.println("  Web 模板生成完成");
    }

    private String getFrontendRootPath() {
        return generatorConfig.getFrontendOutputPath();
    }

    private String getAssetPath(String subPath) {
        return getFrontendRootPath() + "/assets/" + subPath;
    }

    private String getPagePath(String pageName) {
        return getFrontendRootPath() + "/pages/" + pageName;
    }

    // ==================== CSS ====================

    private void generateCss() throws IOException {
        StringBuilder c = new StringBuilder();
        c.append(":root {\n");
        c.append("    --primary: #2563eb;\n");
        c.append("    --primary-hover: #1d4ed8;\n");
        c.append("    --danger: #dc2626;\n");
        c.append("    --danger-hover: #b91c1c;\n");
        c.append("    --success: #16a34a;\n");
        c.append("    --warning: #f59e0b;\n");
        c.append("    --bg: #f1f5f9;\n");
        c.append("    --card-bg: #ffffff;\n");
        c.append("    --text: #1e293b;\n");
        c.append("    --text-muted: #64748b;\n");
        c.append("    --border: #e2e8f0;\n");
        c.append("    --radius: 8px;\n");
        c.append("    --shadow: 0 1px 3px rgba(0,0,0,0.1);\n");
        c.append("    --navbar-bg: #1e293b;\n");
        c.append("}\n\n");
        c.append("*{margin:0;padding:0;box-sizing:border-box}\n");
        c.append("body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:var(--bg);color:var(--text);line-height:1.6;min-height:100vh}\n\n");
        c.append(".navbar{background:var(--navbar-bg);color:#fff;padding:0 24px;height:56px;display:flex;align-items:center;justify-content:space-between;position:sticky;top:0;z-index:100;box-shadow:0 2px 4px rgba(0,0,0,0.2)}\n");
        c.append(".navbar h1{font-size:18px;font-weight:600}\n");
        c.append(".nav-links{display:flex;align-items:center;gap:12px}\n");
        c.append(".nav-links a{color:#cbd5e1;text-decoration:none;padding:6px 12px;border-radius:var(--radius);font-size:14px;transition:all .2s}\n");
        c.append(".nav-links a:hover{color:#fff;background:rgba(255,255,255,0.1)}\n");
        c.append("#userInfo{color:#94a3b8;font-size:13px}\n");
        c.append("#logoutBtn{background:var(--danger);color:#fff;border:none;padding:6px 14px;border-radius:var(--radius);cursor:pointer;font-size:13px;display:none}\n");
        c.append("#logoutBtn:hover{background:var(--danger-hover)}\n\n");
        c.append(".container{max-width:1280px;margin:0 auto;padding:24px 16px}\n");
        c.append(".content{background:var(--card-bg);border-radius:var(--radius);box-shadow:var(--shadow);padding:24px}\n\n");
        c.append(".welcome{margin-bottom:24px}\n");
        c.append(".welcome h2{font-size:24px;margin-bottom:8px}\n");
        c.append(".welcome p{color:var(--text-muted)}\n\n");
        c.append(".menu-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:16px}\n");
        c.append(".menu-card{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:28px 16px;background:var(--card-bg);border:1px solid var(--border);border-radius:var(--radius);text-decoration:none;color:var(--text);transition:all .2s;box-shadow:var(--shadow)}\n");
        c.append(".menu-card:hover{border-color:var(--primary);transform:translateY(-2px);box-shadow:0 4px 12px rgba(37,99,235,0.15)}\n");
        c.append(".menu-card .icon{font-size:32px;margin-bottom:10px}\n");
        c.append(".menu-card .label{font-size:14px;font-weight:500}\n\n");
        c.append(".form-container{max-width:420px;margin:60px auto;background:var(--card-bg);padding:32px;border-radius:var(--radius);box-shadow:var(--shadow)}\n");
        c.append(".form-container h2{margin-bottom:24px;text-align:center;font-size:22px}\n");
        c.append(".form-group{margin-bottom:16px}\n");
        c.append(".form-group label{display:block;margin-bottom:6px;font-size:14px;font-weight:500}\n");
        c.append(".form-group input,.form-group select{width:100%;padding:10px 12px;border:1px solid var(--border);border-radius:var(--radius);font-size:14px;transition:border-color .2s;outline:none}\n");
        c.append(".form-group input:focus,.form-group select:focus{border-color:var(--primary);box-shadow:0 0 0 3px rgba(37,99,235,0.1)}\n\n");
        c.append(".btn{display:inline-flex;align-items:center;gap:6px;padding:10px 20px;background:var(--primary);color:#fff;border:none;border-radius:var(--radius);cursor:pointer;font-size:14px;font-weight:500;transition:background .2s}\n");
        c.append(".btn:hover{background:var(--primary-hover)}\n");
        c.append(".btn-outline{background:transparent;color:var(--primary);border:1px solid var(--primary)}\n");
        c.append(".btn-outline:hover{background:rgba(37,99,235,0.05)}\n");
        c.append(".btn-danger{background:var(--danger)}\n");
        c.append(".btn-danger:hover{background:var(--danger-hover)}\n");
        c.append(".btn-sm{padding:5px 10px;font-size:12px;border-radius:4px}\n");
        c.append(".btn-block{width:100%;justify-content:center}\n\n");
        c.append(".toolbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;flex-wrap:wrap;gap:12px}\n");
        c.append(".toolbar-left{display:flex;gap:8px}\n");
        c.append(".search-box{display:flex;gap:8px}\n");
        c.append(".search-box input{padding:8px 12px;border:1px solid var(--border);border-radius:var(--radius);font-size:14px;min-width:200px;outline:none}\n");
        c.append(".search-box input:focus{border-color:var(--primary)}\n\n");
        c.append("table.data-table{width:100%;border-collapse:collapse}\n");
        c.append(".data-table th,.data-table td{padding:10px 14px;text-align:left;border-bottom:1px solid var(--border);font-size:14px}\n");
        c.append(".data-table th{background:#f8fafc;font-weight:600;color:var(--text-muted);font-size:13px;text-transform:uppercase;letter-spacing:.5px;white-space:nowrap}\n");
        c.append(".data-table tr:hover{background:#f8fafc}\n");
        c.append(".data-table td.actions{display:flex;gap:6px}\n");
        c.append(".table-wrap{overflow-x:auto}\n\n");
        c.append(".pagination{display:flex;justify-content:center;align-items:center;gap:4px;margin-top:20px}\n");
        c.append(".pagination button{width:36px;height:36px;border:1px solid var(--border);background:var(--card-bg);border-radius:var(--radius);cursor:pointer;font-size:13px;transition:all .2s}\n");
        c.append(".pagination button:hover{border-color:var(--primary)}\n");
        c.append(".pagination button.active{background:var(--primary);color:#fff;border-color:var(--primary)}\n");
        c.append(".pagination button:disabled{opacity:.5;cursor:not-allowed}\n\n");
        c.append(".modal-overlay{display:none;position:fixed;z-index:200;left:0;top:0;width:100%;height:100%;background:rgba(0,0,0,0.5);animation:fadeIn .2s}\n");
        c.append(".modal-overlay.show{display:flex;align-items:center;justify-content:center}\n");
        c.append(".modal{background:var(--card-bg);border-radius:var(--radius);padding:28px;width:90%;max-width:520px;max-height:85vh;overflow-y:auto;position:relative;animation:slideUp .25s}\n");
        c.append(".modal .close{position:absolute;right:16px;top:16px;font-size:24px;cursor:pointer;color:var(--text-muted);background:none;border:none;line-height:1}\n");
        c.append(".modal .close:hover{color:var(--text)}\n");
        c.append(".modal h3{margin-bottom:20px;font-size:18px}\n");
        c.append(".modal .form-actions{display:flex;gap:10px;margin-top:20px;justify-content:flex-end}\n\n");
        c.append("@keyframes fadeIn{from{opacity:0}to{opacity:1}}\n");
        c.append("@keyframes slideUp{from{transform:translateY(20px);opacity:0}to{transform:translateY(0);opacity:1}}\n\n");
        c.append(".toast-container{position:fixed;top:20px;right:20px;z-index:300;display:flex;flex-direction:column;gap:8px}\n");
        c.append(".toast{padding:12px 20px;border-radius:var(--radius);color:#fff;font-size:14px;box-shadow:0 4px 12px rgba(0,0,0,0.15);animation:slideIn .3s;max-width:380px;display:flex;align-items:center;gap:8px}\n");
        c.append(".toast.success{background:var(--success)}\n");
        c.append(".toast.error{background:var(--danger)}\n");
        c.append(".toast.warning{background:var(--warning);color:#000}\n");
        c.append(".toast.info{background:var(--primary)}\n");
        c.append("@keyframes slideIn{from{transform:translateX(100%);opacity:0}to{transform:translateX(0);opacity:1}}\n\n");
        c.append(".loading{display:flex;align-items:center;justify-content:center;padding:40px;color:var(--text-muted)}\n");
        c.append(".spinner{width:32px;height:32px;border:3px solid var(--border);border-top-color:var(--primary);border-radius:50%;animation:spin .7s linear infinite;margin-right:10px}\n");
        c.append("@keyframes spin{to{transform:rotate(360deg)}}\n\n");
        c.append(".empty-state{text-align:center;padding:40px;color:var(--text-muted)}\n");
        c.append(".empty-state .icon{font-size:48px;margin-bottom:12px}\n\n");
        c.append("@media(max-width:768px){\n");
        c.append("    .container{padding:12px}\n");
        c.append("    .toolbar{flex-direction:column;align-items:stretch}\n");
        c.append("    .search-box input{min-width:0;flex:1}\n");
        c.append("    .menu-grid{grid-template-columns:repeat(auto-fill,minmax(150px,1fr))}\n");
        c.append("    .form-container{margin:20px;padding:20px}\n");
        c.append("}\n");

        Generator.util.FileUtils.writeToFile(getAssetPath("css/style.css"), c.toString());
    }

    // ==================== JS: app.js ====================

    private void generateAppJs() throws IOException {
        StringBuilder j = new StringBuilder();
        j.append("function getAppBasePath(){\n");
        j.append("    const path=window.location.pathname.replace(/\\\\/g,'/');\n");
        j.append("    const pagesIdx=path.indexOf('/pages/');\n");
        j.append("    if(pagesIdx>=0) return path.substring(0,pagesIdx);\n");
        j.append("    const slashIdx=path.lastIndexOf('/');\n");
        j.append("    return slashIdx>=0?path.substring(0,slashIdx):'';\n");
        j.append("}\n\n");
        j.append("function homeUrl(){\n");
        j.append("    return getAppBasePath()+'/index.html';\n");
        j.append("}\n\n");
        j.append("function pageUrl(page){\n");
        j.append("    return getAppBasePath()+'/pages/'+page;\n");
        j.append("}\n");

        Generator.util.FileUtils.writeToFile(getAssetPath("js/app.js"), j.toString());
    }

    // ==================== JS: api.js ====================

    private void generateApiJs() throws IOException {
        String apiBase = generatorConfig.getApiBase();
        StringBuilder j = new StringBuilder();
        j.append("const API_BASE='").append(apiBase).append("';\n\n");
        j.append("function getHeaders(){\n");
        j.append("    const h={'Content-Type':'application/json'};\n");
        j.append("    const t=localStorage.getItem('token');\n");
        j.append("    if(t) h['Authorization']='Bearer '+t;\n");
        j.append("    return h;\n");
        j.append("}\n\n");
        j.append("async function apiRequest(url,method,data=null){\n");
        j.append("    const opt={method,headers:getHeaders()};\n");
        j.append("    if(data&&method!=='GET') opt.body=JSON.stringify(data);\n");
        j.append("    const res=await fetch(API_BASE+url,opt);\n");
        j.append("    if(res.status===401){localStorage.clear();window.location.href=pageUrl('login.html');return}\n");
        j.append("    if(!res.ok) throw new Error('Request failed: '+res.status);\n");
        j.append("    return await res.json();\n");
        j.append("}\n\n");
        j.append("async function apiGet(url){return await apiRequest(url,'GET')}\n");
        j.append("async function apiPost(url,data){return await apiRequest(url,'POST',data)}\n");
        j.append("async function apiPut(url,data){return await apiRequest(url,'PUT',data)}\n");
        j.append("async function apiDelete(url){return await apiRequest(url,'DELETE')}\n");

        Generator.util.FileUtils.writeToFile(getAssetPath("js/api.js"), j.toString());
    }

    // ==================== JS: auth.js ====================

    private void generateAuthJs() throws IOException {
        StringBuilder j = new StringBuilder();
        j.append("async function login(username,password){\n");
        j.append("    const r=await apiPost('/auth/login',{username,password});\n");
        j.append("    if(r.code===200){localStorage.setItem('token',r.data.token);localStorage.setItem('username',r.data.username)}\n");
        j.append("    return r\n");
        j.append("}\n\n");
        j.append("async function register(username,password,role){\n");
        j.append("    return await apiPost('/auth/register',{username,password,role})\n");
        j.append("}\n\n");
        j.append("function logout(){\n");
        j.append("    localStorage.clear();\n");
        j.append("    window.location.href=pageUrl('login.html');\n");
        j.append("}\n\n");
        j.append("function isLoggedIn(){\n");
        j.append("    return !!localStorage.getItem('token')\n");
        j.append("}\n\n");
        j.append("function getUsername(){\n");
        j.append("    return localStorage.getItem('username')||''\n");
        j.append("}\n\n");
        j.append("function checkAuth(){\n");
        j.append("    const userInfo=document.getElementById('userInfo');\n");
        j.append("    const logoutBtn=document.getElementById('logoutBtn');\n");
        j.append("    if(isLoggedIn()){\n");
        j.append("        if(userInfo) userInfo.textContent=getUsername();\n");
        j.append("        if(logoutBtn){logoutBtn.style.display='inline-block';logoutBtn.onclick=logout}\n");
        j.append("    }\n");
        j.append("}\n");

        Generator.util.FileUtils.writeToFile(getAssetPath("js/auth.js"), j.toString());
    }

    // ==================== JS: toast.js ====================

    private void generateToastJs() throws IOException {
        StringBuilder j = new StringBuilder();
        j.append("let toastId=0;\n\n");
        j.append("function showToast(msg,type='info'){\n");
        j.append("    let container=document.querySelector('.toast-container');\n");
        j.append("    if(!container){container=document.createElement('div');container.className='toast-container';document.body.appendChild(container)}\n");
        j.append("    const el=document.createElement('div');\n");
        j.append("    el.className='toast '+type;\n");
        j.append("    el.id='toast-'+(++toastId);\n");
        j.append("    el.textContent=msg;\n");
        j.append("    container.appendChild(el);\n");
        j.append("    setTimeout(()=>{el.style.opacity='0';el.style.transition='opacity .3s';setTimeout(()=>el.remove(),300)},3000);\n");
        j.append("}\n");

        Generator.util.FileUtils.writeToFile(getAssetPath("js/toast.js"), j.toString());
    }

    // ==================== JS: table.js ====================

    private void generateTableJs() throws IOException {
        StringBuilder j = new StringBuilder();
        j.append("class TableManager{\n");
        j.append("    constructor(cfg){\n");
        j.append("        this.apiUrl=cfg.apiUrl;\n");
        j.append("        this.idField=cfg.idField||'id';\n");
        j.append("        this.columns=cfg.columns;\n");
        j.append("        this.labels=cfg.labels||{};\n");
        j.append("        this.page=0;\n");
        j.append("        this.size=cfg.pageSize||10;\n");
        j.append("        this.totalPages=0;\n");
        j.append("        this.searchVal='';\n");
        j.append("        this.loadData();\n");
        j.append("    }\n\n");
        j.append("    async loadData(){\n");
        j.append("        const tbody=document.getElementById('tableBody');\n");
        j.append("        tbody.innerHTML='<tr><td colspan=\"'+(this.columns.length+1)+'\" class=\"loading\"><div class=\"spinner\"></div> 加载中...</td></tr>';\n");
        j.append("        try{\n");
        j.append("            const r=await apiGet(this.apiUrl+'/page?page='+this.page+'&size='+this.size);\n");
        j.append("            if(r.code===200){this.totalPages=r.data.totalPages;this.renderTable(r.data.content);this.renderPagination()}else{showToast(r.message||'加载失败','error')}\n");
        j.append("        }catch(e){showToast('网络错误: '+e.message,'error')}\n");
        j.append("    }\n\n");
        j.append("    renderTable(data){\n");
        j.append("        const tbody=document.getElementById('tableBody');\n");
        j.append("        if(!data||data.length===0){tbody.innerHTML='<tr><td colspan=\"'+(this.columns.length+1)+'\" class=\"empty-state\"><div class=\"icon\">📋</div>暂无数据</td></tr>';return}\n");
        j.append("        tbody.innerHTML='';\n");
        j.append("        data.forEach(item=>{\n");
        j.append("            const tr=document.createElement('tr');\n");
        j.append("            this.columns.forEach(col=>{\n");
        j.append("                const td=document.createElement('td');\n");
        j.append("                td.textContent=item[col]??'';\n");
        j.append("                tr.appendChild(td);\n");
        j.append("            });\n");
        j.append("            const td=document.createElement('td');td.className='actions';\n");
        j.append("            const editBtn=document.createElement('button');\n");
        j.append("            editBtn.className='btn btn-sm';\n");
        j.append("            editBtn.textContent='编辑';\n");
        j.append("            editBtn.addEventListener('click',()=>this.editItem(item[this.idField]));\n");
        j.append("            const deleteBtn=document.createElement('button');\n");
        j.append("            deleteBtn.className='btn btn-sm btn-danger';\n");
        j.append("            deleteBtn.textContent='删除';\n");
        j.append("            deleteBtn.addEventListener('click',()=>this.deleteItem(item[this.idField]));\n");
        j.append("            td.appendChild(editBtn);\n");
        j.append("            td.appendChild(deleteBtn);\n");
        j.append("            tr.appendChild(td);tbody.appendChild(tr);\n");
        j.append("        });\n");
        j.append("    }\n\n");
        j.append("    renderPagination(){\n");
        j.append("        const p=document.getElementById('pagination');\n");
        j.append("        if(this.totalPages<=1){p.innerHTML='';return}\n");
        j.append("        p.innerHTML='';\n");
        j.append("        const prevBtn=document.createElement('button');\n");
        j.append("        prevBtn.textContent='‹';\n");
        j.append("        prevBtn.disabled=this.page===0;\n");
        j.append("        prevBtn.addEventListener('click',()=>this.goPage(this.page-1));\n");
        j.append("        p.appendChild(prevBtn);\n");
        j.append("        for(let i=0;i<this.totalPages;i++){\n");
        j.append("            const btn=document.createElement('button');\n");
        j.append("            btn.textContent=String(i+1);\n");
        j.append("            if(i===this.page) btn.className='active';\n");
        j.append("            btn.addEventListener('click',()=>this.goPage(i));\n");
        j.append("            p.appendChild(btn);\n");
        j.append("        }\n");
        j.append("        const nextBtn=document.createElement('button');\n");
        j.append("        nextBtn.textContent='›';\n");
        j.append("        nextBtn.disabled=this.page>=this.totalPages-1;\n");
        j.append("        nextBtn.addEventListener('click',()=>this.goPage(this.page+1));\n");
        j.append("        p.appendChild(nextBtn);\n");
        j.append("    }\n\n");
        j.append("    goPage(n){this.page=n;this.loadData()}\n\n");
        j.append("    search(){\n");
        j.append("        const inp=document.getElementById('searchInput');\n");
        j.append("        this.searchVal=inp?inp.value:'';\n");
        j.append("        this.page=0;this.specSearch();\n");
        j.append("    }\n\n");
        j.append("    async specSearch(){\n");
        j.append("        if(!this.searchVal){this.loadData();return}\n");
        j.append("        const tbody=document.getElementById('tableBody');\n");
        j.append("        tbody.innerHTML='<tr><td colspan=\"'+(this.columns.length+1)+'\" class=\"loading\"><div class=\"spinner\"></div> 搜索中...</td></tr>';\n");
        j.append("        try{\n");
        j.append("            const cond={};this.columns.forEach(c=>{cond[c]=this.searchVal});\n");
        j.append("            const r=await apiPost(this.apiUrl+'/search?page='+this.page+'&size='+this.size,cond);\n");
        j.append("            if(r.code===200){this.totalPages=r.data.totalPages;this.renderTable(r.data.content);this.renderPagination()}else{showToast(r.message,'error')}\n");
        j.append("        }catch(e){showToast('搜索失败: '+e.message,'error')}\n");
        j.append("    }\n\n");
        j.append("    openModal(data=null){\n");
        j.append("        document.getElementById('modalTitle').textContent=data?'编辑':'添加';\n");
        j.append("        const idInput=document.getElementById('f_'+this.idField);\n");
        j.append("        if(data){\n");
        j.append("            if(idInput) idInput.value=data[this.idField]??'';\n");
        j.append("            this.columns.forEach(c=>{\n");
        j.append("                const el=document.getElementById('f_'+c);\n");
        j.append("                if(el){\n");
        j.append("                    if(el.type==='checkbox') el.checked=!!data[c];\n");
        j.append("                    else el.value=data[c]??'';\n");
        j.append("                }\n");
        j.append("            });\n");
        j.append("        }else{\n");
        j.append("            document.getElementById('dataForm').reset();\n");
        j.append("            if(idInput) idInput.value='';\n");
        j.append("        }\n");
        j.append("        document.getElementById('modalOverlay').classList.add('show');\n");
        j.append("    }\n\n");
        j.append("    closeModal(){\n");
        j.append("        document.getElementById('modalOverlay').classList.remove('show');\n");
        j.append("    }\n\n");
        j.append("    async handleSubmit(e){\n");
        j.append("        e.preventDefault();\n");
        j.append("        const data={};\n");
        j.append("        const idInput=document.getElementById('f_'+this.idField);\n");
        j.append("        if(idInput&&idInput.value!=='') data[this.idField]=idInput.value;\n");
        j.append("        this.columns.forEach(c=>{\n");
        j.append("            const el=document.getElementById('f_'+c);\n");
        j.append("            if(el){\n");
        j.append("                if(el.type==='checkbox') data[c]=el.checked;\n");
        j.append("                else if(el.value!=='') data[c]=el.value;\n");
        j.append("            }\n");
        j.append("        });\n");
        j.append("        const id=data[this.idField];\n");
        j.append("        try{\n");
        j.append("            const r=id?await apiPut(this.apiUrl+'/'+id,data):await apiPost(this.apiUrl,data);\n");
        j.append("            if(r.code===200){this.closeModal();this.loadData();showToast(id?'更新成功':'创建成功','success')}\n");
        j.append("            else showToast(r.message||'操作失败','error')\n");
        j.append("        }catch(e){showToast('操作失败: '+e.message,'error')}\n");
        j.append("    }\n\n");
        j.append("    async editItem(id){\n");
        j.append("        try{\n");
        j.append("            const r=await apiGet(this.apiUrl+'/'+id);\n");
        j.append("            if(r.code===200) this.openModal(r.data);else showToast(r.message,'error')\n");
        j.append("        }catch(e){showToast('加载失败: '+e.message,'error')}\n");
        j.append("    }\n\n");
        j.append("    async deleteItem(id){\n");
        j.append("        if(!confirm('确定要删除这条数据吗？')) return;\n");
        j.append("        try{\n");
        j.append("            const r=await apiDelete(this.apiUrl+'/'+id);\n");
        j.append("            if(r.code===200){this.loadData();showToast('删除成功','success')}else showToast(r.message,'error')\n");
        j.append("        }catch(e){showToast('删除失败: '+e.message,'error')}\n");
        j.append("    }\n");
        j.append("}\n\n");
        j.append("let table;\n");
        j.append("document.addEventListener('DOMContentLoaded',function(){\n");
        j.append("    checkAuth();\n");
        j.append("    const tbl=document.getElementById('dataTable');\n");
        j.append("    if(!tbl) return;\n");
        j.append("    const idField=tbl.dataset.idField||'id';\n");
        j.append("    const cols=JSON.parse(tbl.dataset.columns||'[]');\n");
        j.append("    const labels=JSON.parse(tbl.dataset.labels||'{}');\n");
        j.append("    table=new TableManager({apiUrl:tbl.dataset.api,idField:idField,columns:cols,labels:labels});\n");
        j.append("    document.getElementById('addBtn').addEventListener('click',()=>table.openModal());\n");
        j.append("    document.getElementById('refreshBtn').addEventListener('click',()=>{table.page=0;table.loadData()});\n");
        j.append("    document.getElementById('dataForm').addEventListener('submit',e=>table.handleSubmit(e));\n");
        j.append("    document.getElementById('searchBtn').addEventListener('click',()=>table.search());\n");
        j.append("    document.getElementById('searchInput').addEventListener('keydown',e=>{if(e.key==='Enter')table.search()});\n");
        j.append("    document.querySelector('.modal .close').addEventListener('click',()=>table.closeModal());\n");
        j.append("    document.getElementById('cancelBtn').addEventListener('click',()=>table.closeModal());\n");
        j.append("    document.getElementById('modalOverlay').addEventListener('click',function(e){if(e.target===this)table.closeModal()});\n");
        j.append("});\n");

        Generator.util.FileUtils.writeToFile(getAssetPath("js/table.js"), j.toString());
    }

    // ==================== HTML: index.html ====================

    private void generateIndexHtml(List<TableInfo> tables) throws IOException {
        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n");
        h.append("<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n");
        h.append("<title>管理系统</title>\n<link rel=\"stylesheet\" href=\"assets/css/style.css\">\n</head>\n<body>\n");
        h.append("<nav class=\"navbar\"><h1>管理系统</h1><div class=\"nav-links\">");
        h.append("<a href=\"index.html\">首页</a><a href=\"pages/login.html\">登录</a><a href=\"pages/register.html\">注册</a>");
        h.append("<span id=\"userInfo\"></span><button id=\"logoutBtn\">退出</button></div></nav>\n");
        h.append("<div class=\"container\"><div class=\"content\"><div class=\"welcome\"><h2>欢迎使用管理系统</h2>");
        h.append("<p>选择一个模块开始管理数据</p></div><div class=\"menu-grid\" id=\"menuList\"></div></div></div>\n");
        h.append("<script src=\"assets/js/app.js\"></script>\n<script src=\"assets/js/api.js\"></script>\n<script src=\"assets/js/auth.js\"></script>\n<script src=\"assets/js/toast.js\"></script>\n<script>\n");
        h.append("document.addEventListener('DOMContentLoaded',function(){checkAuth();\n");
        h.append("const menu=document.getElementById('menuList');\n");
        h.append("const tables=[");
        for (int i = 0; i < tables.size(); i++) {
            if (i > 0) h.append(",");
            String entityName = Generator.util.StringUtils.toCamelCase(tables.get(i).getTableName(), false);
            String tableName = tables.get(i).getTableName();
            String title = getTableTitle(tables.get(i));
            h.append("{name:'").append(escapeJs(title)).append("',file:'").append(entityName).append("'}");
        }
        h.append("];\n");
        h.append("tables.forEach(t=>{const a=document.createElement('a');\n");
        h.append("a.href='pages/'+t.file+'.html';a.className='menu-card';\n");
        h.append("a.innerHTML='<span class=\"icon\">📊</span><span class=\"label\">'+t.name+'</span>';\n");
        h.append("menu.appendChild(a);});\n");
        h.append("});\n</script>\n</body>\n</html>\n");

        Generator.util.FileUtils.writeToFile(getFrontendRootPath() + "/index.html", h.toString());
    }

    // ==================== HTML: login.html ====================

    private void generateLoginHtml() throws IOException {
        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n");
        h.append("<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n");
        h.append("<title>登录</title>\n<link rel=\"stylesheet\" href=\"../assets/css/style.css\">\n</head>\n<body>\n");
        h.append("<div class=\"container\"><div class=\"form-container\"><h2>登录</h2>\n");
        h.append("<form id=\"loginForm\">\n");
        h.append("<div class=\"form-group\"><label for=\"username\">用户名</label><input type=\"text\" id=\"username\" required></div>\n");
        h.append("<div class=\"form-group\"><label for=\"password\">密码</label><input type=\"password\" id=\"password\" required></div>\n");
        h.append("<button type=\"submit\" class=\"btn btn-block\">登录</button>\n</form>\n");
        h.append("<p style=\"text-align:center;margin-top:16px;font-size:14px\">还没有账号？<a href=\"register.html\">注册</a></p></div></div>\n");
        h.append("<script src=\"../assets/js/app.js\"></script>\n<script src=\"../assets/js/api.js\"></script>\n<script src=\"../assets/js/auth.js\"></script>\n<script src=\"../assets/js/toast.js\"></script>\n<script>\n");
        h.append("document.getElementById('loginForm').addEventListener('submit',async function(e){\n");
        h.append("e.preventDefault();const u=document.getElementById('username').value;const p=document.getElementById('password').value;\n");
        h.append("try{const r=await login(u,p);if(r.code===200){showToast('登录成功','success');setTimeout(()=>window.location.href=homeUrl(),800)}");
        h.append("else showToast(r.message,'error')}catch(err){showToast('登录失败: '+err.message,'error')}\n");
        h.append("});\n</script>\n</body>\n</html>\n");

        Generator.util.FileUtils.writeToFile(getPagePath("login.html"), h.toString());
    }

    // ==================== HTML: register.html ====================

    private void generateRegisterHtml() throws IOException {
        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n");
        h.append("<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n");
        h.append("<title>注册</title>\n<link rel=\"stylesheet\" href=\"../assets/css/style.css\">\n</head>\n<body>\n");
        h.append("<div class=\"container\"><div class=\"form-container\"><h2>注册</h2>\n");
        h.append("<form id=\"registerForm\">\n");
        h.append("<div class=\"form-group\"><label for=\"username\">用户名</label><input type=\"text\" id=\"username\" required></div>\n");
        h.append("<div class=\"form-group\"><label for=\"password\">密码</label><input type=\"password\" id=\"password\" required></div>\n");
        h.append("<div class=\"form-group\"><label for=\"role\">角色</label><select id=\"role\"><option value=\"")
                .append(escapeHtmlAttribute(authConfig.getRoleUser())).append("\">用户</option><option value=\"")
                .append(escapeHtmlAttribute(authConfig.getRoleAdmin())).append("\">管理员</option></select></div>\n");
        h.append("<button type=\"submit\" class=\"btn btn-block\">注册</button>\n</form>\n");
        h.append("<p style=\"text-align:center;margin-top:16px;font-size:14px\">已有账号？<a href=\"login.html\">登录</a></p></div></div>\n");
        h.append("<script src=\"../assets/js/app.js\"></script>\n<script src=\"../assets/js/api.js\"></script>\n<script src=\"../assets/js/auth.js\"></script>\n<script src=\"../assets/js/toast.js\"></script>\n<script>\n");
        h.append("document.getElementById('registerForm').addEventListener('submit',async function(e){\n");
        h.append("e.preventDefault();const u=document.getElementById('username').value;const p=document.getElementById('password').value;");
        h.append("const rl=document.getElementById('role').value;\n");
        h.append("try{const r=await register(u,p,rl);if(r.code===200){showToast('注册成功','success');setTimeout(()=>window.location.href=pageUrl('login.html'),800)}");
        h.append("else showToast(r.message,'error')}catch(err){showToast('注册失败: '+err.message,'error')}\n");
        h.append("});\n</script>\n</body>\n</html>\n");

        Generator.util.FileUtils.writeToFile(getPagePath("register.html"), h.toString());
    }

    // ==================== HTML: {table}.html ====================

    private void generateTableHtml(TableInfo table) throws IOException {
        String tableName = table.getTableName();
        String className = Generator.util.StringUtils.toCamelCase(tableName, true);
        String entityName = Generator.util.StringUtils.toCamelCase(tableName, false);

        ColumnInfo idColumn = getPrimaryKeyColumn(table);
        String idField = idColumn != null
                ? Generator.util.StringUtils.toCamelCase(idColumn.getColumnName(), false)
                : "id";

        List<ColumnInfo> uiColumns = new ArrayList<>();
        for (ColumnInfo col : table.getColumns()) {
            if (col.isPrimaryKey() || isExcludedField(col) || isAutoFillField(col)) {
                continue;
            }
            uiColumns.add(col);
        }

        StringBuilder columnNames = new StringBuilder("[");
        StringBuilder columnLabels = new StringBuilder("{");
        boolean first = true;
        for (ColumnInfo col : uiColumns) {
            String fn = Generator.util.StringUtils.toCamelCase(col.getColumnName(), false);
            if (!first) { columnNames.append(","); columnLabels.append(","); }
            first = false;
            columnNames.append("\"").append(fn).append("\"");
            columnLabels.append("\"").append(fn).append("\":\"").append(escapeJson(getColumnLabel(col))).append("\"");
        }
        columnNames.append("]");
        columnLabels.append("}");

        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n");
        h.append("<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n");
        h.append("<title>").append(escapeHtml(getTableTitle(table))).append(" 管理</title>\n<link rel=\"stylesheet\" href=\"../assets/css/style.css\">\n</head>\n<body>\n");
        h.append("<nav class=\"navbar\"><h1>").append(className).append(" 管理</h1><div class=\"nav-links\">");
        h.append("<a href=\"../index.html\">首页</a><span id=\"userInfo\"></span><button id=\"logoutBtn\">退出</button></div></nav>\n");
        h.append("<div class=\"container\"><div class=\"content\">\n");
        h.append("<div class=\"toolbar\"><div class=\"toolbar-left\">");
        h.append("<button id=\"addBtn\" class=\"btn\">+ 添加</button><button id=\"refreshBtn\" class=\"btn btn-outline\">刷新</button></div>");
        h.append("<div class=\"search-box\"><input type=\"text\" id=\"searchInput\" placeholder=\"按关键字段搜索...\"><button id=\"searchBtn\" class=\"btn\">搜索</button></div></div>\n");
        h.append("<div class=\"table-wrap\">\n<table id=\"dataTable\" class=\"data-table\" data-api=\"/").append(entityName).append("\" data-id-field=\"").append(idField).append("\" data-columns='").append(columnNames).append("' data-labels='").append(columnLabels).append("'>\n");
        h.append("<thead><tr>\n");
        for (ColumnInfo col : uiColumns) {
            h.append("<th>").append(escapeHtml(getColumnLabel(col))).append("</th>\n");
        }
        h.append("<th>操作</th></tr></thead>\n<tbody id=\"tableBody\"></tbody>\n</table>\n</div>\n");
        h.append("<div id=\"pagination\" class=\"pagination\"></div>\n</div></div>\n\n");

        h.append("<div id=\"modalOverlay\" class=\"modal-overlay\"><div class=\"modal\">");
        h.append("<button class=\"close\">&times;</button><h3 id=\"modalTitle\">添加</h3>\n");
        h.append("<form id=\"dataForm\">\n");
        h.append("<input type=\"hidden\" id=\"f_").append(idField).append("\">\n");
        for (ColumnInfo col : uiColumns) {
            String fn = Generator.util.StringUtils.toCamelCase(col.getColumnName(), false);
            String type = getHtmlInputType(col.getColumnType());
            h.append("<div class=\"form-group\"><label for=\"f_").append(fn).append("\">").append(escapeHtml(getColumnLabel(col))).append("</label>");
            h.append("<input type=\"").append(type).append("\" id=\"f_").append(fn).append("\"");
            if (!col.isNullable()) {
                h.append(" required");
            }
            h.append("></div>\n");
        }
        h.append("<div class=\"form-actions\">");
        h.append("<button type=\"button\" id=\"cancelBtn\" class=\"btn btn-outline\">取消</button>");
        h.append("<button type=\"submit\" class=\"btn\">保存</button></div>\n");
        h.append("</form></div></div>\n\n");

        h.append("<script src=\"../assets/js/app.js\"></script>\n<script src=\"../assets/js/api.js\"></script>\n<script src=\"../assets/js/auth.js\"></script>\n");
        h.append("<script src=\"../assets/js/toast.js\"></script>\n<script src=\"../assets/js/table.js\"></script>\n</body>\n</html>\n");

        Generator.util.FileUtils.writeToFile(getPagePath(entityName + ".html"), h.toString());
    }

    private String getHtmlInputType(String columnType) {
        switch (columnType.toUpperCase()) {
            case "INT": case "BIGINT": case "SMALLINT": case "TINYINT":
            case "DECIMAL": case "DOUBLE": case "FLOAT":
                return "number";
            case "DATE": return "date";
            case "DATETIME": case "TIMESTAMP": return "datetime-local";
            case "BOOLEAN": case "BIT": return "checkbox";
            default: return "text";
        }
    }

    private ColumnInfo getPrimaryKeyColumn(TableInfo table) {
        for (ColumnInfo column : table.getColumns()) {
            if (column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }

    private boolean isExcludedField(ColumnInfo column) {
        String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
        return generatorConfig.getExcludedFields().contains(fieldName)
                || generatorConfig.getExcludedFields().contains(column.getColumnName());
    }

    private boolean isAutoFillField(ColumnInfo column) {
        String type = column.getColumnType().toUpperCase();
        if (!"DATETIME".equals(type) && !"TIMESTAMP".equals(type) && !"DATE".equals(type)) {
            return false;
        }
        String name = column.getColumnName().toLowerCase();
        return name.startsWith("create_") || name.startsWith("created_")
                || name.startsWith("update_") || name.startsWith("updated_");
    }

    private String getColumnLabel(ColumnInfo column) {
        if (column.getComment() != null && !column.getComment().trim().isEmpty()) {
            return column.getComment().trim();
        }
        return formatName(column.getColumnName());
    }

    private String getTableTitle(TableInfo table) {
        if (table.getTableComment() != null && !table.getTableComment().trim().isEmpty()) {
            return table.getTableComment().trim();
        }
        return formatName(table.getTableName());
    }

    private String formatName(String rawName) {
        String[] parts = rawName.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String escapeHtmlAttribute(String value) {
        return escapeHtml(value);
    }

    private String escapeJs(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"");
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
