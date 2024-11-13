#!/usr/bin/env bash

set -euo pipefail

# enable debug
# set -x

LOCALSTACK_URL="http://localhost:4566"

create_queue() {
	local QUEUE_NAME_TO_CREATE=$1

	AWS_ACCESS_KEY_ID=fakeAccessKey AWS_SECRET_ACCESS_KEY=fakeSecretKey \
	aws --endpoint-url "${LOCALSTACK_URL}" sqs create-queue \
	--queue-name "${QUEUE_NAME_TO_CREATE}" \
	--region us-east-1
}

create_queue "payment"
create_queue "return-payment"
create_queue "cancel-subscription"

create_queue "signature"
create_queue "return-signature"