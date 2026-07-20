import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = "https://api.conx.co.kr";

const COMPANY_TOKEN = __ENV.COMPANY_TOKEN;
const CREW_TOKEN = __ENV.CREW_TOKEN;

const TEST_IMAGE =
"https://conx-dev-s3-files-394489192136-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/uploads/ed2c8de6-5cb3-4ef1-914e-be1a4211c4a5-12312.jpeg";

const INDUSTRIES = [
    "IT",
    "BEAUTY",
    "LIFESTYLE",
    "ETC"
];

const PROJECT_TYPES = [
    "APPTEST",
    "MARKETING",
    "CAMPAIGN"
];

const CREW_TYPES = [
    "CLUB",
    "ETC",
    "COUNCIL"
];

export const options = {
    stages: [
        { duration: '1m', target: 20 }, // 워밍업
        { duration: '6m', target: 40 },  // 9분 동안 유지
        { duration: '1m', target: 10 },  // 종료
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],
        http_req_failed: ["rate<0.01"],
    }

};

function randomBool(){
    return Math.random()<0.5;
}

function randomImages(){
    return randomBool()?null:[TEST_IMAGE];
}

function randomFiles(){

    if(randomBool()) return null;

    return [
        {
            originalName:"12312.jpeg",
            fileLinks:TEST_IMAGE,
            explanation:"k6"
        }
    ];
}

function randomLinks(){

    if(randomBool()) return null;

    return [
        {
            linkName:"Github",
            link:"https://github.com",
            explanation:"repo"
        }
    ];
}

function createProject(){

    const payload = JSON.stringify({

        brandName:`Brand-${__VU}-${__ITER}`,
        managerName:"Tester",
        managerEmail:`tester${__VU}${__ITER}@test.com`,

        projectImages:randomImages(),

        projectName:`k6-${Date.now()}-${__VU}-${__ITER}`,

        projectExplanation:"Performance Test",

        industry:
            INDUSTRIES[Math.floor(Math.random()*INDUSTRIES.length)],

        projectType:
            PROJECT_TYPES[Math.floor(Math.random()*PROJECT_TYPES.length)],

        resultForm:[
            {
                platform:"Instagram",
                contentType:"Feed",
                numberOfResult:3,
                finalResult:"PDF"
            }
        ],

        recruitDeadline:"2026-08-30",
        projectStartDate:"2026-09-01",
        projectDeadline:"2026-09-30",
        submitDeadline:"2026-10-05",

        subsidy:500000,

        incentive:false,

        incentiveCondition:null,

        crewType:
            CREW_TYPES[Math.floor(Math.random()*CREW_TYPES.length)],

        peopleNumber:3,

        competency:"Spring Boot",

        preferenceCondition:"Backend",

        fileLinks:randomFiles(),

        additionalLinks:randomLinks()

    });

    const res = http.post(

        `${BASE_URL}/api/v1/companies/me/projects?isDraft=false`,

        payload,

        {

            headers:{
                Authorization:`Bearer ${COMPANY_TOKEN}`,
                "Content-Type":"application/json"
            },

            tags:{
                endpoint:"project-create"
            }

        }

    );

    check(res,{
        "create success":r=>r.status==200||r.status==201
    });

    if (res.status !== 200 && res.status !== 201) {
        console.log(`status=${res.status}`);
        console.log(res.body);
    }

}

function browse(){

    const page=Math.floor(Math.random()*3);

    const res = http.get(

    `${BASE_URL}/api/v1/projects?page=${page}&size=10`,

    {

        headers:{
            Authorization:`Bearer ${CREW_TOKEN}`
        },

        tags:{
            endpoint:"project-list"
        }

    });

    check(res,{
        "browse success":r=>r.status==200
    });

    if (res.status !== 200 && res.status !== 201) {
        console.log(`status=${res.status}`);
        console.log(res.body);

    }

}

function getProjectDetail(){

    const projectId=
        Math.floor(Math.random()*7)+1;

    const payload=JSON.stringify({

        motivation:`performance-test-${__VU}-${__ITER}`

    });

    const res=http.get(

        `${BASE_URL}/api/v1/projects/${projectId}`,

        {

            headers:{
                Authorization:`Bearer ${CREW_TOKEN}`,
                "Content-Type":"application/json"
            },

            tags:{
                endpoint:"project-apply"
            }

        }

    );

    check(res,{
        "detail success":r=>r.status==200||r.status==201
    });

    if (res.status !== 200 && res.status !== 201) {
        console.log(`status=${res.status}`);
        console.log(res.body);
    }

}

export default function(){

    const r=Math.random();

    if(r<0.6){

        browse();

    }else if(r>0.9){

        createProject();

    }else{

        getProjectDetail();

    }

    sleep(0.3);

}