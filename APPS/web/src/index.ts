import express from 'express'
import cors from 'cors'
import { createServer } from 'http'
import { router } from './services/router'
import 'dotenv/config'
import io from './services/socket'
import path from 'path'

const app = express()
const server = createServer(app)

const PORT = process.env.PORT || 3000


app.use(cors({ origin: '*' }))
app.use(express.json())
app.use(express.urlencoded({ extended: true }))
app.use(express.static(path.join(__dirname, 'public')))
app.use('/api', router)


io.attach(server);

server.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`)
})

