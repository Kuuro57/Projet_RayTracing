#! /bin/bash

cur_dir=$(dirname $(readlink -f $0))
cur_dir=$(pwd)
app_dir="${cur_dir}"
app_file="noeud.jar"


ssh_cmd="ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=accept-new"
username=$(whoami)

server="localhost"
port="1099"
room="pc125"
kill_cmd="no"
java_cmd="java"


# dans bin/noeud        ../../deploy-nodes.sh -s 100.64.80.224 -a LancerNoeudCalcul 
# dans rmi-avec-div    ./deploy-nodes.sh -s 100.64.80.224 -a dist/noeud.jar -j 'java -jar'


usage () {
    echo >&2 "Run raytracer node on many machines at IUT
Usage: `basename $0` [-k] [-a|--app java-class] [-s|--server server] [-p|--port] [-r|--room room] [-j|--java java-cmd]
    -a    The java class or jar    
    -j    The java cmd 
    -s    The server name/IP
    -p    The registry port
    -r    The IUT room used
    -k    Kill rather then run"
    
    exit 1
}

while [[ $# -gt 0 ]]; do
    case $1 in
	-a|--app)
	    app_file=$2
	    shift
	    ;;
	-j|--java)
	    java_cmd=$2
	    shift
	    ;;
        -s|--server)
            server=$2
            shift 
            ;;
        -p|--port)
            port=$2
            shift
            ;;
        -r|--room)
            room=$2
            shift
            ;;
        -k|--kill)
            kill_cmd="yes"
            shift
            ;;
        -*|--*|*)
            usage
            ;;
    esac
    shift
done

ip_prefix="100.64.80"
case $room in
    pc125)
        ip_prefix="100.64.80"
        min_ip="224"
        max_ip="246"
        node_list=$(seq -f "${ip_prefix}.%g" ${min_ip} ${max_ip})
        ;;
     pc127)
        ip_prefix="100.64.80"
        min_ip="208"
        max_ip="223"
        node_list=$(seq -f "${ip_prefix}.%g" ${min_ip} ${max_ip})
        ;;
    local)
        node_list=$(yes "127.0.0.1" | head -n 10)
        ;;
    *)
        echo "Unsuported yet set room to pc125 or local"
        exit 1
        ;;
esac
    
app_cmd="cd ${app_dir} && ${java_cmd} ${app_file}"
    
for node in $node_list ; do 
        
    if [[ $kill_cmd == "yes" ]]; then
        echo "killing on ${node}"
        ${ssh_cmd} ${node} "killall -u ${username} java"
    else
        echo "launching on ${node}"
        ${ssh_cmd} ${node} "${app_cmd} ${server} ${port} &" &
    fi
    
    sleep .3
done





